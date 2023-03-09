package propensi.pmosystem.service;

import propensi.pmosystem.model.BusinessModel;

import java.util.List;

public interface BusinessService {

    BusinessModel addBusiness(BusinessModel business);

    BusinessModel updateBusiness(BusinessModel business);

    BusinessModel deleteBusiness(BusinessModel business);

    List<BusinessModel> getListBusiness();

    BusinessModel getBusinessById(Long id);
}
