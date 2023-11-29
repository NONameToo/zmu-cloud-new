package com.zmu.cloud.demos.producer.jpa.dao;

import com.zmu.cloud.demos.producer.jpa.entity.po.Product;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProductMapper extends PagingAndSortingRepository<Product, String> {
}