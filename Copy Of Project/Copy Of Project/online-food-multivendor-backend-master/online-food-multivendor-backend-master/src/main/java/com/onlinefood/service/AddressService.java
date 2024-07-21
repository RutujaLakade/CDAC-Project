package com.onlinefood.service;

import java.util.List;

import com.onlinefood.entity.Address;
import com.onlinefood.entity.User;

public interface AddressService {
	
	Address addAddress(Address address);
	
	Address updateAddress(Address address);
	
	Address getAddressById(int addressId);

}
