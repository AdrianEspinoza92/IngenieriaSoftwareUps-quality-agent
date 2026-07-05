package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Cita médica confirmada
 */


@Schema(name = "CitaResponse", description = "Cita médica confirmada")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class CitaResponse {

  private UUID id;

  private UUID pacienteId;

  private String medicoNombre;

  private String especialidad;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate fecha;

  private String horaInicio;

  private String horaFin;

  /**
   * Gets or Sets estado
   */
  public enum EstadoEnum {
    CONFIRMADA("CONFIRMADA"),
    
    CANCELADA("CANCELADA"),
    
    COMPLETADA("COMPLETADA");

    private String value;

    EstadoEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EstadoEnum fromValue(String value) {
      for (EstadoEnum b : EstadoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private EstadoEnum estado;

  private String numeroReferencia;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime creadaEn;

  public CitaResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CitaResponse(UUID id, UUID pacienteId, String medicoNombre, String especialidad, LocalDate fecha, String horaInicio, String horaFin, EstadoEnum estado, String numeroReferencia, OffsetDateTime creadaEn) {
    this.id = id;
    this.pacienteId = pacienteId;
    this.medicoNombre = medicoNombre;
    this.especialidad = especialidad;
    this.fecha = fecha;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
    this.estado = estado;
    this.numeroReferencia = numeroReferencia;
    this.creadaEn = creadaEn;
  }

  public CitaResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public CitaResponse pacienteId(UUID pacienteId) {
    this.pacienteId = pacienteId;
    return this;
  }

  /**
   * Get pacienteId
   * @return pacienteId
  */
  @NotNull @Valid 
  @Schema(name = "pacienteId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pacienteId")
  public UUID getPacienteId() {
    return pacienteId;
  }

  public void setPacienteId(UUID pacienteId) {
    this.pacienteId = pacienteId;
  }

  public CitaResponse medicoNombre(String medicoNombre) {
    this.medicoNombre = medicoNombre;
    return this;
  }

  /**
   * Get medicoNombre
   * @return medicoNombre
  */
  @NotNull 
  @Schema(name = "medicoNombre", example = "Dra. Ana Torres", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("medicoNombre")
  public String getMedicoNombre() {
    return medicoNombre;
  }

  public void setMedicoNombre(String medicoNombre) {
    this.medicoNombre = medicoNombre;
  }

  public CitaResponse especialidad(String especialidad) {
    this.especialidad = especialidad;
    return this;
  }

  /**
   * Get especialidad
   * @return especialidad
  */
  @NotNull 
  @Schema(name = "especialidad", example = "Medicina General", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("especialidad")
  public String getEspecialidad() {
    return especialidad;
  }

  public void setEspecialidad(String especialidad) {
    this.especialidad = especialidad;
  }

  public CitaResponse fecha(LocalDate fecha) {
    this.fecha = fecha;
    return this;
  }

  /**
   * Get fecha
   * @return fecha
  */
  @NotNull @Valid 
  @Schema(name = "fecha", example = "Tue Jul 14 19:00:00 ECT 2026", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fecha")
  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public CitaResponse horaInicio(String horaInicio) {
    this.horaInicio = horaInicio;
    return this;
  }

  /**
   * Get horaInicio
   * @return horaInicio
  */
  @NotNull 
  @Schema(name = "horaInicio", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("horaInicio")
  public String getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(String horaInicio) {
    this.horaInicio = horaInicio;
  }

  public CitaResponse horaFin(String horaFin) {
    this.horaFin = horaFin;
    return this;
  }

  /**
   * Get horaFin
   * @return horaFin
  */
  @NotNull 
  @Schema(name = "horaFin", example = "09:30", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("horaFin")
  public String getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(String horaFin) {
    this.horaFin = horaFin;
  }

  public CitaResponse estado(EstadoEnum estado) {
    this.estado = estado;
    return this;
  }

  /**
   * Get estado
   * @return estado
  */
  @NotNull 
  @Schema(name = "estado", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("estado")
  public EstadoEnum getEstado() {
    return estado;
  }

  public void setEstado(EstadoEnum estado) {
    this.estado = estado;
  }

  public CitaResponse numeroReferencia(String numeroReferencia) {
    this.numeroReferencia = numeroReferencia;
    return this;
  }

  /**
   * Número de referencia único de la cita
   * @return numeroReferencia
  */
  @NotNull 
  @Schema(name = "numeroReferencia", example = "CIT-2026-00001", description = "Número de referencia único de la cita", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("numeroReferencia")
  public String getNumeroReferencia() {
    return numeroReferencia;
  }

  public void setNumeroReferencia(String numeroReferencia) {
    this.numeroReferencia = numeroReferencia;
  }

  public CitaResponse creadaEn(OffsetDateTime creadaEn) {
    this.creadaEn = creadaEn;
    return this;
  }

  /**
   * Timestamp de creación en UTC
   * @return creadaEn
  */
  @NotNull @Valid 
  @Schema(name = "creadaEn", description = "Timestamp de creación en UTC", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("creadaEn")
  public OffsetDateTime getCreadaEn() {
    return creadaEn;
  }

  public void setCreadaEn(OffsetDateTime creadaEn) {
    this.creadaEn = creadaEn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CitaResponse citaResponse = (CitaResponse) o;
    return Objects.equals(this.id, citaResponse.id) &&
        Objects.equals(this.pacienteId, citaResponse.pacienteId) &&
        Objects.equals(this.medicoNombre, citaResponse.medicoNombre) &&
        Objects.equals(this.especialidad, citaResponse.especialidad) &&
        Objects.equals(this.fecha, citaResponse.fecha) &&
        Objects.equals(this.horaInicio, citaResponse.horaInicio) &&
        Objects.equals(this.horaFin, citaResponse.horaFin) &&
        Objects.equals(this.estado, citaResponse.estado) &&
        Objects.equals(this.numeroReferencia, citaResponse.numeroReferencia) &&
        Objects.equals(this.creadaEn, citaResponse.creadaEn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, pacienteId, medicoNombre, especialidad, fecha, horaInicio, horaFin, estado, numeroReferencia, creadaEn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CitaResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    pacienteId: ").append(toIndentedString(pacienteId)).append("\n");
    sb.append("    medicoNombre: ").append(toIndentedString(medicoNombre)).append("\n");
    sb.append("    especialidad: ").append(toIndentedString(especialidad)).append("\n");
    sb.append("    fecha: ").append(toIndentedString(fecha)).append("\n");
    sb.append("    horaInicio: ").append(toIndentedString(horaInicio)).append("\n");
    sb.append("    horaFin: ").append(toIndentedString(horaFin)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    numeroReferencia: ").append(toIndentedString(numeroReferencia)).append("\n");
    sb.append("    creadaEn: ").append(toIndentedString(creadaEn)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

