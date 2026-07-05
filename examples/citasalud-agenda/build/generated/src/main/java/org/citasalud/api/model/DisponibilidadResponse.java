package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Disponibilidad de un médico en una fecha específica
 */


@Schema(name = "DisponibilidadResponse", description = "Disponibilidad de un médico en una fecha específica")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class DisponibilidadResponse {

  private UUID medicoId;

  private String medicoNombre;

  private String especialidad;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate fecha;

  @Valid
  private List<@Valid FranjaHorariaResponse> franjas = new ArrayList<>();

  public DisponibilidadResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DisponibilidadResponse(UUID medicoId, String medicoNombre, String especialidad, LocalDate fecha, List<@Valid FranjaHorariaResponse> franjas) {
    this.medicoId = medicoId;
    this.medicoNombre = medicoNombre;
    this.especialidad = especialidad;
    this.fecha = fecha;
    this.franjas = franjas;
  }

  public DisponibilidadResponse medicoId(UUID medicoId) {
    this.medicoId = medicoId;
    return this;
  }

  /**
   * Get medicoId
   * @return medicoId
  */
  @NotNull @Valid 
  @Schema(name = "medicoId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("medicoId")
  public UUID getMedicoId() {
    return medicoId;
  }

  public void setMedicoId(UUID medicoId) {
    this.medicoId = medicoId;
  }

  public DisponibilidadResponse medicoNombre(String medicoNombre) {
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

  public DisponibilidadResponse especialidad(String especialidad) {
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

  public DisponibilidadResponse fecha(LocalDate fecha) {
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

  public DisponibilidadResponse franjas(List<@Valid FranjaHorariaResponse> franjas) {
    this.franjas = franjas;
    return this;
  }

  public DisponibilidadResponse addFranjasItem(FranjaHorariaResponse franjasItem) {
    if (this.franjas == null) {
      this.franjas = new ArrayList<>();
    }
    this.franjas.add(franjasItem);
    return this;
  }

  /**
   * Lista de franjas horarias ordenadas por horaInicio
   * @return franjas
  */
  @NotNull @Valid 
  @Schema(name = "franjas", description = "Lista de franjas horarias ordenadas por horaInicio", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("franjas")
  public List<@Valid FranjaHorariaResponse> getFranjas() {
    return franjas;
  }

  public void setFranjas(List<@Valid FranjaHorariaResponse> franjas) {
    this.franjas = franjas;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DisponibilidadResponse disponibilidadResponse = (DisponibilidadResponse) o;
    return Objects.equals(this.medicoId, disponibilidadResponse.medicoId) &&
        Objects.equals(this.medicoNombre, disponibilidadResponse.medicoNombre) &&
        Objects.equals(this.especialidad, disponibilidadResponse.especialidad) &&
        Objects.equals(this.fecha, disponibilidadResponse.fecha) &&
        Objects.equals(this.franjas, disponibilidadResponse.franjas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(medicoId, medicoNombre, especialidad, fecha, franjas);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DisponibilidadResponse {\n");
    sb.append("    medicoId: ").append(toIndentedString(medicoId)).append("\n");
    sb.append("    medicoNombre: ").append(toIndentedString(medicoNombre)).append("\n");
    sb.append("    especialidad: ").append(toIndentedString(especialidad)).append("\n");
    sb.append("    fecha: ").append(toIndentedString(fecha)).append("\n");
    sb.append("    franjas: ").append(toIndentedString(franjas)).append("\n");
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

