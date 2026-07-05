package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Franja horaria de la agenda de un médico
 */


@Schema(name = "FranjaHorariaResponse", description = "Franja horaria de la agenda de un médico")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class FranjaHorariaResponse {

  private UUID id;

  private String horaInicio;

  private String horaFin;

  /**
   * Estado actual de la franja
   */
  public enum EstadoEnum {
    DISPONIBLE("DISPONIBLE"),
    
    OCUPADA("OCUPADA");

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

  public FranjaHorariaResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FranjaHorariaResponse(UUID id, String horaInicio, String horaFin, EstadoEnum estado) {
    this.id = id;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
    this.estado = estado;
  }

  public FranjaHorariaResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Identificador único de la franja
   * @return id
  */
  @NotNull @Valid 
  @Schema(name = "id", description = "Identificador único de la franja", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public FranjaHorariaResponse horaInicio(String horaInicio) {
    this.horaInicio = horaInicio;
    return this;
  }

  /**
   * Hora de inicio en formato HH:mm
   * @return horaInicio
  */
  @NotNull @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$") 
  @Schema(name = "horaInicio", example = "09:00", description = "Hora de inicio en formato HH:mm", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("horaInicio")
  public String getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(String horaInicio) {
    this.horaInicio = horaInicio;
  }

  public FranjaHorariaResponse horaFin(String horaFin) {
    this.horaFin = horaFin;
    return this;
  }

  /**
   * Hora de fin en formato HH:mm (siempre horaInicio + 30 min)
   * @return horaFin
  */
  @NotNull @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$") 
  @Schema(name = "horaFin", example = "09:30", description = "Hora de fin en formato HH:mm (siempre horaInicio + 30 min)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("horaFin")
  public String getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(String horaFin) {
    this.horaFin = horaFin;
  }

  public FranjaHorariaResponse estado(EstadoEnum estado) {
    this.estado = estado;
    return this;
  }

  /**
   * Estado actual de la franja
   * @return estado
  */
  @NotNull 
  @Schema(name = "estado", description = "Estado actual de la franja", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("estado")
  public EstadoEnum getEstado() {
    return estado;
  }

  public void setEstado(EstadoEnum estado) {
    this.estado = estado;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FranjaHorariaResponse franjaHorariaResponse = (FranjaHorariaResponse) o;
    return Objects.equals(this.id, franjaHorariaResponse.id) &&
        Objects.equals(this.horaInicio, franjaHorariaResponse.horaInicio) &&
        Objects.equals(this.horaFin, franjaHorariaResponse.horaFin) &&
        Objects.equals(this.estado, franjaHorariaResponse.estado);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, horaInicio, horaFin, estado);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FranjaHorariaResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    horaInicio: ").append(toIndentedString(horaInicio)).append("\n");
    sb.append("    horaFin: ").append(toIndentedString(horaFin)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
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

