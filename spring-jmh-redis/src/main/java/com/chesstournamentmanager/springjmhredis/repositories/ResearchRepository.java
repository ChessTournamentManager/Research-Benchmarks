package com.chesstournamentmanager.springjmhredis.repositories;

import com.chesstournamentmanager.springjmhredis.models.Research;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResearchRepository extends CrudRepository<Research, UUID> {

}
