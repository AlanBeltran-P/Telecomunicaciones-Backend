package com.telecom.facturacion.notificacion.mapper;

import com.telecom.facturacion.notificacion.dto.NotificacionResponse;
import com.telecom.facturacion.notificacion.entity.Notificacion;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface NotificacionMapper {

    NotificacionResponse aNotificacionResponse(Notificacion notificacion);

    List<NotificacionResponse> aNotificacionResponses(List<Notificacion> notificaciones);
}
