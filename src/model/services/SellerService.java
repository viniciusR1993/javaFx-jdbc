package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){
		return dao.findAll();	//Essa implementa�ao foi feita em projeto anterior
		/*MOCK (Estou retornando s� os dados ficticios)
		List<Seller> list = new ArrayList<>();
		list.add(new Seller(1,"Books"));
		list.add(new Seller(2,"Computer"));
		list.add(new Seller(3,"Eletronics"));
		return list;*/
	}
	
	public void saveOrUpdate(Seller obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) {
		dao.delete(obj.getId());
	}

}
