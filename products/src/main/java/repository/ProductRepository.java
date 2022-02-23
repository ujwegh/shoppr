package repository;

import model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

//@RepositoryRestResource(collectionResourceRel = "users", path = "users") - проверить при запуске чо это за покемон
public interface ProductRepository extends JpaRepository<Product, Long> {

}
