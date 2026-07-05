package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

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
 * Datos necesarios para reservar una cita
 */


@Schema(name = "ReservarCitaRequest", description = "Datos necesarios para reservar una cita")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class ReservarCitaRequest {

  private UUID pacienteId;

  private UUID medicoId;

  private UUID franjaHorariaId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate fecha;

  public ReservarCitaRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservarCitaRequest(UUID pacienteId, UUID medicoId, UUID franjaHorariaId, LocalDate fecha) {
    this.pacienteId = pacienteId;
    this.medicoId = medicoId;
    this.franjaHorariaId = franjaHorariaId;
    this.fecha = fecha;
  }

  public ReservarCitaRequest pacienteId(UUID pacienteId) {
    this.pacienteId = pacienteId;
    return this;
  }

  /**
   * Identificador del paciente autenticado
   * @return pacienteId
  */
  @NotNull @Valid 
  @Schema(name = "pacienteId", description = "Identificador del paciente autenticado", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pacienteId")
  public UUID getPacienteId() {
    return pacienteId;
  }

  public void setPacienteId(UUID pacienteId) {
    this.pacienteId = pacienteId;
  }

  public ReservarCitaRequest medicoId(UUID medicoId) {
    this.medicoId = medicoId;
    return this;
  }

  /**
   * Identificador del médico seleccionado
   * @return medicoId
  */
  @NotNull @Valid 
  @Schema(name = "medicoId", description = "Identificador del médico seleccionado", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("medicoId")
  public UUID getMedicoId() {
    return medicoId;
  }

  public void setMedicoId(UUID medicoId) {
    this.medicoId = medicoId;
  }

  public ReservarCitaRequest franjaHorariaId(UUID franjaHorariaId) {
    this.franjaHorariaId = franjaHorariaId;
    return this;
  }

  /**
   * Identificador de la franja horaria a reservar
   * @return franjaHorariaId
  */
  @NotNull @Valid 
  @Schema(name = "franjaHorariaId", description = "Identificador de la franja horaria a reservar", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("franjaHorariaId")
  public UUID getFranjaHorariaId() {
    return franjaHorariaId;
  }

  public void setFranjaHorariaId(UUID franjaHorariaId) {
    this.franjaHorariaId = franjaHorariaId;
  }

  public ReservarCitaRequest fecha(LocalDate fecha) {
    this.fecha = fecha;
    return this;
  }

  /**
   * Fecha de la cita (debe coincidir con la fecha de la franja)
   * @return fecha
  */
  @NotNull @Valid 
  @Schema(name = "fecha", example = "Tue Jul 14 19:00:00 ECT 2026", description = "Fecha de la cita (debe coincidir con la fecha de la franja)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fecha")
  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservarCitaRequest reservarCitaRequest = (ReservarCitaRequest) o;
    return Objects.equals(this.pacienteId, reservarCitaRequest.pacienteId) &&
        Objects.equals(this.medicoId, reservarCitaRequest.medicoId) &&
        Objects.equals(this.franjaHorariaId, reservarCitaRequest.franjaHorariaId) &&
        Objects.equals(this.fecha, reservarCitaRequest.fecha);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pacienteId, medicoId, franjaHorariaId, fecha);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservarCitaRequest {\n");
    sb.append("    pacienteId: ").append(toIndentedString(pacienteId)).append("\n");
    sb.append("    medicoId: ").append(toIndentedString(medicoId)).append("\n");
    sb.append("    franjaHorariaId: ").append(toIndentedString(franjaHorariaId)).append("\n");
    sb.append("    fecha: ").append(toIndentedString(fecha)).append("\n");
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

