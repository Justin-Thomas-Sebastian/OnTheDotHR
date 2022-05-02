package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository <Category, Long> {

}
