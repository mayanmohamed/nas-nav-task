package nas.nav.task.models;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "uploads")
public class Picture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String title;

	@Column
	private String description;

	@Type(type="org.hibernate.type.BinaryType")
	@Column
	@Lob
	private byte[] attachment;

	@Column
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column
	private String urls;

	@Column
	@Enumerated(EnumType.STRING)
	private Category category;

	@ManyToOne
	private User user;

	@Column
	private String OriginalFilename;
}
