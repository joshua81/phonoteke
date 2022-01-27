package org.phonoteke.loader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HumanBeatsBatch {

  public static void main(String[] args) throws Exception {
    System.exit(SpringApplication.exit(SpringApplication.run(HumanBeatsBatch.class, args)));
  }
}
