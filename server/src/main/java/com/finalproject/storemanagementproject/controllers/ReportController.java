package com.finalproject.storemanagementproject.controllers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalproject.storemanagementproject.models.APIResponse;
import com.finalproject.storemanagementproject.models.AnalyticsReport;
import com.finalproject.storemanagementproject.services.AnalyticsService;
import com.finalproject.storemanagementproject.services.ProductService;

@RestController
@RequestMapping("/reports")
public class ReportController {
	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private ProductService productService;

	@GetMapping("/sale-results")
	public ResponseEntity<APIResponse<AnalyticsReport>> getReportsForPeriod(
			@RequestParam(required = false, defaultValue = "today") String timeline,
			@RequestParam(required = false) Instant startDate,
			@RequestParam(required = false) Instant endDate) {
		try {
			AnalyticsReport analyticsReport = analyticsService.getReportByTimeLine(timeline, startDate, endDate);
			return ResponseEntity.ok(
					new APIResponse<>(HttpStatus.OK.value(), "Success", Collections.singletonList(analyticsReport)));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "There is something wrong...", null));
		}
	}
}
