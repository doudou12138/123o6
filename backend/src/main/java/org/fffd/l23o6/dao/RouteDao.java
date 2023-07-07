package org.fffd.l23o6.dao;

import io.lettuce.core.dynamic.annotation.Param;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RouteDao extends JpaRepository<RouteEntity, Long>{
    //@Query("SELECT r.id FROM RouteEntity r " +
    //        "WHERE :startStationId IN r.stationIds " +
    //        "AND :endStationId IN r.stationIds " +
    //        "AND INDEX(:startStationId) < INDEX(:endStationId)")
    @Query("select r from RouteEntity r")
    List<RouteEntity> findAll();

    //@Query("select r.id from RouteEntity r")
    //where :startStationId member of r.stationIds and :endStationId member of r.stationIds")
    //List<Integer> findAllId(Long startStationId, Long endStationId);
}
