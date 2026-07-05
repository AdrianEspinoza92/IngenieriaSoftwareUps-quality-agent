package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Información de un médico registrado en el sistema
 */


@Schema(name = "MedicoResponse", description = "Información de un médico registrado en el sistema")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class MedicoResponse {

  private UUID id;

  private String nombre;

  private String especialidad;

  public MedicoResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MedicoResponse(UUID id, String nombre, String especialidad) {
    this.id = id;
    this.nombre = nombre;
    this.especialidad = especialidad;
  }

  public MedicoResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Identificador único del médico
   * @return id
  */
  @NotNull @Valid 
  @Schema(name = "id", description = "Identificador único del médico", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public MedicoResponse nombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  /**
   * Nombre completo del médico
   * @return nombre
  */
  @NotNull 
  @Schema(name = "nombre", example = "Dra. Ana Torres", description = "Nombre completo del médico", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nombre")
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public MedicoResponse especialidad(String especialidad) {
    this.especialidad = especialidad;
    return this;
  }

  /**
   * Especialidad médica
   * @return especialidad
  */
  @NotNull 
  @Schema(name = "especialidad", example = "Medicina General", description = "Especialidad médica", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("especialidad")
  public String getEspecialidad() {
    return especialidad;
  }

  public void setEspecialidad(String especialidad) {
    this.especialidad = especialidad;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MedicoResponse medicoResponse = (MedicoResponse) o;
    return Objects.equals(this.id, medicoResponse.id) &&
        Objects.equals(this.nombre, medicoResponse.nombre) &&
        Objects.equals(this.especialidad, medicoResponse.especialidad);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nombre, especialidad);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MedicoResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    especialidad: ").append(toIndentedString(especialidad)).append("\n");
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

