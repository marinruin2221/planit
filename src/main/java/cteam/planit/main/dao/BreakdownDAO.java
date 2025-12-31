package cteam.planit.main.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BREAKDOWN")
@SequenceGenerator
(
	name = "breakdown_seq_gen",
	sequenceName = "BREAKDOWN_SEQ",
	allocationSize = 1
)
@Data
public class BreakdownDAO
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "breakdown_seq_gen")
	private Long id;
	private Long usersId;
	private String name;
	private String dateF;
	private String dateT;
	private String status;
	private int isActive = 0;
}