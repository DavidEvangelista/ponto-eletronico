package br.com.ais.pontoeletronico.domain

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Timesheet.
 */
@Entity
@Table(name = "timesheet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Timesheet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "weekday", columnDefinition = "varchar(8)")
    var weekday: LocalDate? = null,

    @Column(name = "checkin", columnDefinition = "varchar(8)")
    var checkin: LocalTime? = null,

    @Column(name = "go_lunch", columnDefinition = "varchar(8)")
    var goLunch: LocalTime? = null,

    @Column(name = "back_lunch", columnDefinition = "varchar(8)")
    var backLunch: LocalTime? = null,

    @Column(name = "checkout", columnDefinition = "varchar(8)")
    var checkout: LocalTime? = null,

    @Column(name = "total")
    var total: Long? = null,

    @Column(name = "user_id")
    var userId: Long? = null,

    @ManyToOne
    @JoinColumn(name = "project_id")
    var project: Project? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Timesheet) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Timesheet{" +
        "id=$id" +
        ", weekday='$weekday'" +
        ", goLunch='$goLunch'" +
        ", backLunch='$backLunch'" +
        ", checkin='$checkin'" +
        ", checkout='$checkout'" +
        ", total='$total'" +
        ", userId=$userId" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
