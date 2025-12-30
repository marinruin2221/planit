package cteam.planit.main.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RandomUtil {
  @Value("${spring.security.jwt.dummy}")
  String randomChars;

  public Integer gerInteger(Integer min, Integer max) {
    return min + (int)(Math.random() * ((max - min) + 1));
  }

  public String getString(Integer length) {
    StringBuilder sb = new StringBuilder();
    Integer chatLength = (int)randomChars.length();
    for(int i = 0; i < length; i += 1) {
      int index = (int)gerInteger(0, chatLength - 1);
      sb.append(randomChars.charAt(index));
    }
    return sb.toString();
  }
}

