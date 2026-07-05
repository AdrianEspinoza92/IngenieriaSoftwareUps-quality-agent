package org.citasalud.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Respuesta de error estándar
 */


@Schema(name = "ErrorResponse", description = "Respuesta de error estándar")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-27T12:19:01.139067-05:00[America/Guayaquil]", comments = "Generator version: 7.4.0")
public class ErrorResponse {

  private String codigo;

  private String mensaje;

  @Valid
  private List<@Valid FranjaHorariaResponse> franjasAlternativas;

  public ErrorResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ErrorResponse(String codigo, String mensaje) {
    this.codigo = codigo;
    this.mensaje = mensaje;
  }

  public ErrorResponse codigo(String codigo) {
    this.codigo = codigo;
    return this;
  }

  /**
   * Código de error identificable para el cliente
   * @return codigo
  */
  @NotNull 
  @Schema(name = "codigo", example = "FRANJA_OCUPADA", description = "Código de error identificable para el cliente", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("codigo")
  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public ErrorResponse mensaje(String mensaje) {
    this.mensaje = mensaje;
    return this;
  }

  /**
   * Descripción del error legible para el usuario
   * @return mensaje
  */
  @NotNull 
  @Schema(name = "mensaje", example = "La franja horaria seleccionada ya no está disponible", description = "Descripción del error legible para el usuario", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mensaje")
  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public ErrorResponse franjasAlternativas(List<@Valid FranjaHorariaResponse> franjasAlternativas) {
    this.franjasAlternativas = franjasAlternativas;
    return this;
  }

  public ErrorResponse addFranjasAlternativasItem(FranjaHorariaResponse franjasAlternativasItem) {
    if (this.franjasAlternativas == null) {
      this.franjasAlternativas = new ArrayList<>();
    }
    this.franjasAlternativas.add(franjasAlternativasItem);
    return this;
  }

  /**
   * Franjas alternativas disponibles (solo cuando codigo=FRANJA_OCUPADA)
   * @return franjasAlternativas
  */
  @Valid 
  @Schema(name = "franjasAlternativas", description = "Franjas alternativas disponibles (solo cuando codigo=FRANJA_OCUPADA)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("franjasAlternativas")
  public List<@Valid FranjaHorariaResponse> getFranjasAlternativas() {
    return franjasAlternativas;
  }

  public void setFranjasAlternativas(List<@Valid FranjaHorariaResponse> franjasAlternativas) {
    this.franjasAlternativas = franjasAlternativas;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.codigo, errorResponse.codigo) &&
        Objects.equals(this.mensaje, errorResponse.mensaje) &&
        Objects.equals(this.franjasAlternativas, errorResponse.franjasAlternativas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo, mensaje, franjasAlternativas);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    sb.append("    codigo: ").append(toIndentedString(codigo)).append("\n");
    sb.append("    mensaje: ").append(toIndentedString(mensaje)).append("\n");
    sb.append("    franjasAlternativas: ").append(toIndentedString(franjasAlternativas)).append("\n");
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

