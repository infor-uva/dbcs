package com.uva.api.users.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uva.api.users.api.HotelApi;
import com.uva.api.users.models.Manager;
import com.uva.api.users.models.User;
import com.uva.api.users.repositories.ManagerRepository;
import com.uva.api.users.utils.Utils;

@Service
public class ManagerService {

  @Autowired
  private HotelApi hotelApi;

  @Autowired
  private ManagerRepository managerRepository;

  public Manager save(User request) {
    Manager hm = new Manager();
    BeanUtils.copyProperties(request, hm);
    return managerRepository.save(hm);
  }

  public List<Manager> findAll() {
    return managerRepository.findAll();
  }

  public Manager findById(int id) {
    Manager manager = Utils.assertUser(managerRepository.findById(id));
    return manager;
  }

  public Object deleteById(Integer id) {
    Manager manager = Utils.assertUser(managerRepository.findById(id));
    hotelApi.deleteAllByManagerId(id);
    managerRepository.delete(manager);
    return manager;
  }
}
