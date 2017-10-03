package com.ct.test.downloader;

import com.ct.test.downloader.validation.ConfigValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

/*
Unit Tests will produce a report in builds/tests/test
 */

@RunWith(MockitoJUnitRunner.class)
public class DownloaderApplicationTests {


	@Test
	public void testValidUrl() {

		ConfigValidator validator = new ConfigValidator();
		String validUrl = "http://www.google.com/";
		assertThat(validator.isUrlValid(validUrl)).isTrue();
	}

	@Test
	public void testInValidUrl() {
		ConfigValidator validator = new ConfigValidator();
		String validUrl = "http:/www.google.com/";
		assertThat(validator.isUrlValid(validUrl)).isFalse();
	}


}
