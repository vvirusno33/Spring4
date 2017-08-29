package sample.di.dataaccess;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import sample.di.business.domain.Product;
import sample.di.business.service.ProductDao;

@Repository
public class ProductDaoImpl implements ProductDao {

	// RDB의 대체
	private Map<String, Product> storage = new HashMap<String, Product>();

    // Dao이지만 단순화 하기 위해 RDB에는 액세스 하지 않음
	@Cacheable(value = "area")
    public Product findProduct(String name) {
    	slowly(); // 고의로 지연시킴
        return storage.get(name);
    }

	//@CacheEvict(value = "product", allEntries = true) 모든 캐시 엔트리 삭제
	@CacheEvict(value = "area", key = "#product.name")
	public void addProduct(Product product) {
		storage.put(product.getName(), product);
	}

    private void slowly() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}