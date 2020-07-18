package com.jeffreytinnea.gridwalker.controller;

import com.jeffreytinnea.gridwalker.model.Grid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <h1>Grid Controller</h1>
 * Class to handle routes for grid operations.
 * 
 * @author Jeffrey Tinnea
 * @version 1.0
 */
@RestController
public class GridController {
	/**
	 * Traverses a 2D matrix starting at 0,0 and visiting each node
	 * once in a clockwise, spiraling pattern.
	 * Example request body:
	 * {"matrix":[[0,1,2],[3,4,5],[6,7,8]]}
	 * @param grid Grid object for the 2D matrix of numbers
	 * @return String value of the traversed matrix in order.
	 * In the example above this would be 0,1,2,5,8,7,6,3,4,5
	 */
	@PostMapping(value="/api/traversed-grid", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTraversedGrid(@RequestBody Grid grid) {
		if (grid == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must supply a grid");
		try {
			return ResponseEntity.ok(grid.traverseMatrix());
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}