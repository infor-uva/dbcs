package com.uva.api.bookings.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uva.api.bookings.services.BookingService;

@Component
public class MyScheduledTasks {

  @Autowired
  private BookingService bookingService;

  @Scheduled(cron = "0 30 0 * * *") // Se ejecuta cada día media hora después de medianoche
  public void updateInactiveBookings() {
    System.out.println(
        "Iniciando proceso de actualizar comunicación de cambio de estado para usuarios cuyas reservas finalizaron el dia de hoy");
    long start = System.currentTimeMillis();
    long updatedUsers = bookingService.performDailyClientsStateUpdate();
    long time = System.currentTimeMillis() - start;

    System.out.println("Task Complete! " + updatedUsers + " clients updated in " + time + " ms");
  }
}