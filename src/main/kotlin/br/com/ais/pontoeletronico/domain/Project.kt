package br.com.ais.pontoeletronico.domain

import java.io.Serializable
import java.time.LocalTime
import javax.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Project(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "allocated_hours", columnDefinition = "varchar(8)")
    var allocatedHours: LocalTime? = null,

    @Column(name = "manager_id")
    var manager: Long? = null,

    @Column(name = "description")
    var description: String? = null,

    @OneToMany(mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var timesheets: MutableSet<Timesheet>? = mutableSetOf(),

    @Column(name = "user_id")
    var userId: Long? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Project{" +
        "id=$id" +
        ", name='$name'" +
        ", allocatedHours=$allocatedHours" +
        ", manager=$manager" +
        ", description='$description'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
