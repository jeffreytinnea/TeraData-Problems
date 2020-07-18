package com.jeffreytinnea.gridwalker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h1>Grid Walker</h1>
 * This applicaiton traverses a 2D matrix in a spiral pattern. It starts from (0,0) 
 * and moves clockwise touching each node only once.
 * 
 * @author Jeffrey Tinnea
 * @version 1.0
 */
@SpringBootApplication
public class GridWalker {

	/**
     * Starts the web service
     * @param args Not used.
     */
	public static void main(String[] args) {
		SpringApplication.run(GridWalker.class, args);
	}

}
