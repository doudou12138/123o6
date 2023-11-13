# 实验过后能快速回想到的收获:
1. linux使用更加熟练,本次实验让我印象仍较深的命令有:
     1. 快速换源配置命令(参考南大镜像源)
    2. 内存管理命令 free等
    3. 磁盘管理扩容 fd等
        1. 运行 fdisk /dev/vda 扩容 /dev/vda1 到 30G（删除分区后重建，保留签名）
        2. pvresize /dev/vda1
        3. lvextend -l +100%FREE /dev/ubuntu-vg/root
        4. resize2fs /dev/mapper/ubuntu--vg-root
    4. 文件编辑意外退出的正确处理;因为没有及时把中断的文件编辑彻底关掉,本地java -v显示了java 17;但是南大git runner跑起来打印版本仍是java 8
      5. 多个环境变量配置文件的优先级
    6. 打包gradle项目和打包vue项目
    7. ssh连接并执行命令
    ```
    sshpass -p "${DEPLOY_SERVER_PASSWORD}" scp -o StrictHostKeyChecking=no build/libs/l23o6-0.0.1-SNAPSHOT.jar root@"${DEPLOY_SERVER_IP}":~

    sshpass -p "${DEPLOY_SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no root@"${DEPLOY_SERVER_IP}" "killall java; screen -d -m /usr/java/jdk-17.0.8/bin/java -jar l23o6-0.0.1-SNAPSHOT.jar"
    ```
完了,印象较深刻的就这些了

# devops过程:
## 准备:
承载平台: 南京大学软件学院云平台，南大git gitlab平台
准备gitlab-runner并与项目连接.  
## 服务端:
### 1. 构建与测试
打jar包: 记得给x权限  
测试报告整合到网页端:  借助junit框架(即在gradle中加入他的依赖),在./gradlew test时能够在build/test-result/test/下生成...xml文件.  
gitlab追踪这个文件并单元测试报告整合到流水线中,效果如下:  
![]()   
对应的配置文件的修改:  
```yml
unit-test-job:   # This job runs in the test stage.
  stage: test    # It only starts when the job in the build stage completes successfully.
  script:
    - echo "Running unit tests... This will take about a lot of seconds."
    - chmod +x gradlew
    - ./gradlew test
    - echo "unit tests finish successfully"
  artifacts:
    when: always
    paths:
      - build/test-results/test
#      - build/reports/jacoco/test/
    reports:
      junit: build/test-results/test/*.xml
```

覆盖率报告整合://todo

### 2. 代码质量检测:
效果如图:  
![]()  
借助docker镜像完成,  
```yml
ode-quality:   # This job also runs in the test stage.
  stage: test    # It can run at the same time as unit-test-job (in parallel).
  image: docker:20.10.12
  allow_failure: true
  services:
    - name: 'docker:20.10.12-dind'
      command: ['--tls=false', '--host=tcp://0.0.0.0:2375']
  variables:
    DOCKER_DRIVER: overlay2
    DOCKER_CERT_PATH: ""
    DOCKER_TLS_CERTDIR: ""
    DOCKER_TLS_VERIFY: ""
    CODE_QUALITY_IMAGE_TAG: "0.96.0"
    CODE_QUALITY_IMAGE: "$CI_TEMPLATE_REGISTRY_HOST/gitlab-org/ci-cd/codequality:$CODE_QUALITY_IMAGE_TAG"
    DOCKER_SOCKET_PATH: /var/run/docker.sock
  script:
    - echo "Code quality check start... This will take a lot of seconds."
    - export SOURCE_CODE=$PWD
    - |
      if ! docker info &>/dev/null; then
        if [ -z "$DOCKER_HOST" ] && [ -n "$KUBENETES_PORT" ]; then
          export DOCKER_HOST='tcp://localhost:2375'
        fi
      fi
    - | # this is required to avoid undesirable reset of Docker image ENV variables being set on build stage
      function propagate_env_vars(){
        CURRENT_ENV=$(printenv)
      
        for VAR_NAME; do
          echo $CURRENT_ENV | grep "${VAR_NAME}=" > /dev/null && echo "--env $VAR_NAME"
        done
      }
    - |
      if [ -n "$CODECLIMATE_REGISTRY_USERNAME" ] && [ -n "$CODECLIMATE_REGISTRY_PASSWORD" ] && [ -n "$CODECLIMATE_PREFIX" ]; then
        CODECLIMATE_REGISTRY=${CODECLIMATE_PREFIX%%/*}
        docker login "$CODECLIMATE_REGISTRY" --username 
        "$CODECLIMATE_REGISTRY_USERNAME" --password 
        "$CODECLIMATE_REGISTRY_PASSWORD"
      fi
    - docker pull --quiet "$CODE_QUALITY_IMAGE"
    - |
      docker run --rm \
        $(propagate_env_vars \
          SOURCE_CODE \
          TIMEOUT_SECONDS \
          CODECLIMATE_DEBUG \
          CODECLIMATE_DEV \
          REPORT_STDOUT \
          REPORT_FORMAT \
          ENGINE_MEMORY_LIMIT_BYTES \
          CODECLIMATE_PREFIX \
          CODECLIMATE_REGISTRY_USERNAME \
          CODECLIMATE_REGISTRY_PASSWORD \
          DOCKER_SOCKET_PATH \
        ) \
        --volume "$PWD":/code \
        --volume "$DOCKER_SOCKET_PATH":/var/run/docker.sock \
        "$CODE_QUALITY_IMAGE" /code
    - cat gl-code-quality-report.json
    - |
      if grep "issue" gl-code-quality-report.json
      then
        echo "Test fail"
        exit 1
      else
        echo "Test pass"
        exit 0
      fi
    - echo "COde quality check finish."
  artifacts:
    reports:
      codequality: gl-code-quality-report.json
    paths:
      - gl-code-quality-report.json
    expire_in: 1 week
  dependencies: []
  rules:
    - if: '$CODE_QUALITY_DISABLED'
      when: never
    - if: '$CI_COMMIT_TAG || $CI_COMMIT_BRANCH'
```
3. 代码质量门禁  
在setting中对mergeRequest进行设置,设置merger的分支代码流水线必须success即可实现门禁.  
4. 静态代码安全检查和依赖检查  
依旧是借助docker实现,  
静态代码安全检查输出:  
[图片]  
依赖检查效果:(licenses旁边的Secutiry就是静态安全检查给流水线上的效果)  
[图片]  
对应的配置文件:  
```yml
include:
  - template: SAST.gitlab-ci.yml
  - template: Dependency-Scanning.gitlab-ci.yml

gemnasium-maven-dependency_scanning:
  tags: [ docker ]

semgrep-sast:
  tags: [ docker ]
```
> tags:标签,为阶段指定特定的runner,在runner中可以设置负责运行的相关标签,通过在流水线的阶段选择不同的标签就可选择对应的runner.  
不加标签: 在runner可以设置该runner为不加标签的阶段运行.
可以理解为默认runner,不加标签的交给默认的

5. 部署  
环境准备:  
jdk什么的,你部署所需要的  
手动部署是打包,移包,运行jar包;这里就是让每一次提交时机器自动做一遍这个操作:  
对应的配置文件:  
```yml
deploy-job:      # This job runs in the deploy stage.
  stage: deploy  # It only runs when *both* jobs in the test stage complete successfully.
  environment: production
  script:
    - echo "Deploying application..."
    - sshpass -p "${DEPLOY_SERVER_PASSWORD}" scp -o StrictHostKeyChecking=no build/libs/l23o6-0.0.1-SNAPSHOT.jar root@"${DEPLOY_SERVER_IP}":~
    - sshpass -p "${DEPLOY_SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no root@"${DEPLOY_SERVER_IP}" "killall java; screen -d -m java -jar l23o6-0.0.1-SNAPSHOT.jar"
    - echo "Application successfully deployed."
```

## 网页端
1. 构建  
使用npm命令构建  
2. 代码质量检查  
同服务端  
3. 代码自动格式化  
上面所提到的操作均是代码提交后,在runner机器上进行的,并有可能反馈到gitlab流水线上.  
而这一步发生在开发者自己的电脑上,目的是开发者提交自己电脑上的代码时,本地先完成代码自动格式化,保证传上去的代码格式是良好的.  
4. 静态安全检查和依赖检查  
同服务端  
5. 前端部署  
这里使用nginx进行部署,具体操作是:  
下载好nginx,将runner上打包形成的dist文件夹移到部署服务器上,调整nginx配置文件,包括不限于设置监听端口号,指定dist文件夹位置,开启反向代理等...  
注意: 这些操作均是做一次即可(需要调整再调整),即不是每次流水线都要重新设置.  
流水线配置中要干的事情:  
将更改后的前端项目重新打包,并用新的dist文件夹覆盖原本的dist文件夹(部署服务器上)  
对应的流水线配置文件:  
```yml
sshpass -p "${DEPLOY_WEB_PASSWORD}" scp -o StrictHostKeyChecking=no -r ${WEB_DIR}/dist/ root@172.29.4.166:/usr/vue/
```

#  一些奇怪的bug:
1. 在服务端部署中:  
流水线部署阶段没有报错,但是使用postman发现服务似乎并没有启动起来.  

错误定位:  
登录部署服务器,查看8080端口有无被监听  
```
lsof -i:8080  
```
发现没有被监听.  
```
killall java
```
报没有java程序运行.  
所以jar包确实没有被运行起来.  
在部署服务器中手动输入:  
```
screen -d -m java -jar l23o6-0.0.1-SNAPSHOT.jar
```
发现应用程序跑起来了,OK.重新杀掉该进程.    
到runner中手动输入:  
```
sshpass -p "${DEPLOY_SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no root@"${DEPLOY_SERVER_IP}" "killall java; java -jar l23o6-0.0.1-SNAPSHOT.jar"
```
居然报错了,java not fonud. but java环境变量确实配了.  
不优雅的解决办法:   
将java改成具体的java路径。/usr/java/bin/java -jar ....jar。跑起来了  

2. 部署服务器xshell不上，三人的xshell同时掉线了。  
ping该服务器ping的通  
重启服务器，okfine。完成了。  

3. 构建阶段  
原本安装的jdk8，并配置了环境变量。后安装jdk17，某次更改环境变量的过程，编辑文件异常退出了，后面重新编辑报其他进程在写，没管选择edit，配置成jdk17.   
Java -v：17，ok。  
流水线进行构建，报版本不兼容，用了8？我丢  
定位：在流水线中`echo $JAVA_HOME`,jdk8？？？嗯？？  
解决：编辑环境变量，把之前那个异常退出的编辑类似于推掉或者覆盖的选择。重新运行流水线，  
·echo $JAVA_HOME·,jdk17.构建成功。  
