package cteam.planit.main.dto;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userTable")
@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "userSeq", sequenceName = "userSeq")
public class User implements UserDetails, OAuth2User, Principal {
  @Id
  @GeneratedValue(generator = "userSeq", strategy = GenerationType.SEQUENCE)
  public Long id;

  public String username;
  public String password;

  public String displayname;

  @Transient
  public Map<String, Object> attributes;

  @ElementCollection(fetch = FetchType.EAGER)
  public List<String> roles;

  public String email;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> auth = new ArrayList<>();
    for (String role : roles) {
      auth.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    return auth;
  }

  @Override
  public Map<String, Object> getAttributes() {
    if( attributes == null ) attributes = new HashMap<String, Object>();
    return attributes;
  }

  @Override
  public String getName() {
    return username;
  }

}
