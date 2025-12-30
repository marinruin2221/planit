package cteam.planit.main.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USERS")
@SequenceGenerator
(
	name = "users_seq_gen",
	sequenceName = "USERS_SEQ",
	allocationSize = 1
)
@Data
public class UsersDAO
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
	private Long id;
	private String userId;
	private String userPw;
	private String name;
	private String email;
	private String birthY;
	private String birthM;
	private String birthD;
	private String gender;
	private int isActive = 0;
}