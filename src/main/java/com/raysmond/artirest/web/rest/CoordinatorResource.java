package com.raysmond.artirest.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.raysmond.artirest.domain.Coordinator;

import com.raysmond.artirest.repository.CoordinatorRepository;
import com.raysmond.artirest.web.rest.errors.BadRequestAlertException;
import com.raysmond.artirest.web.rest.util.HeaderUtil;
import com.raysmond.artirest.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Coordinator.
 */
@RestController
@RequestMapping("/api")
public class CoordinatorResource {

    private final Logger log = LoggerFactory.getLogger(CoordinatorResource.class);

    private static final String ENTITY_NAME = "coordinator";

    private final CoordinatorRepository coordinatorRepository;

    public CoordinatorResource(CoordinatorRepository coordinatorRepository) {
        this.coordinatorRepository = coordinatorRepository;
    }

    /**
     * POST  /coordinators : Create a new coordinator.
     *
     * @param coordinator the coordinator to create
     * @return the ResponseEntity with status 201 (Created) and with body the new coordinator, or with status 400 (Bad Request) if the coordinator has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/coordinators")
    @Timed
    public ResponseEntity<Coordinator> createCoordinator(@RequestBody Coordinator coordinator) throws URISyntaxException {
        log.debug("REST request to save Coordinator : {}", coordinator);
        if (coordinator.getId() != null) {
            throw new BadRequestAlertException("A new coordinator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Coordinator result = coordinatorRepository.save(coordinator);
        return ResponseEntity.created(new URI("/api/coordinators/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /coordinators : Updates an existing coordinator.
     *
     * @param coordinator the coordinator to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated coordinator,
     * or with status 400 (Bad Request) if the coordinator is not valid,
     * or with status 500 (Internal Server Error) if the coordinator couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/coordinators")
    @Timed
    public ResponseEntity<Coordinator> updateCoordinator(@RequestBody Coordinator coordinator) throws URISyntaxException {
        log.debug("REST request to update Coordinator : {}", coordinator);
        if (coordinator.getId() == null) {
            return createCoordinator(coordinator);
        }
        Coordinator result = coordinatorRepository.save(coordinator);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, coordinator.getId().toString()))
            .body(result);
    }

    /**
     * GET  /coordinators : get all the coordinators.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of coordinators in body
     */
    @GetMapping("/coordinators")
    @Timed
    public ResponseEntity<List<Coordinator>> getAllCoordinators(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Coordinators");
        Page<Coordinator> page = coordinatorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/coordinators");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /coordinators/:id : get the "id" coordinator.
     *
     * @param id the id of the coordinator to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the coordinator, or with status 404 (Not Found)
     */
    @GetMapping("/coordinators/{id}")
    @Timed
    public ResponseEntity<Coordinator> getCoordinator(@PathVariable String id) {
        log.debug("REST request to get Coordinator : {}", id);
        Coordinator coordinator = coordinatorRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(coordinator));
    }

    /**
     * DELETE  /coordinators/:id : delete the "id" coordinator.
     *
     * @param id the id of the coordinator to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/coordinators/{id}")
    @Timed
    public ResponseEntity<Void> deleteCoordinator(@PathVariable String id) {
        log.debug("REST request to delete Coordinator : {}", id);
        coordinatorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
