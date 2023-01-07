package com.chesstournamentmanager.springjmhmongo.repositories;

import com.chesstournamentmanager.springjmhmongo.models.Research;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResearchRepository extends MongoRepository<Research, UUID> {

}
