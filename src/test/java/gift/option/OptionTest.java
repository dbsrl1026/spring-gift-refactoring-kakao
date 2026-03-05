package gift.option;

import gift.category.Category;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OptionTest {

    @Test
    @DisplayName("calculateTotalPrice: 상품 가격 * 주문 수량을 반환한다")
    void calculateTotalPrice() {
        // Given
        Category category = new Category("카테고리", "#000", "http://img.com", "설명");
        Product product = new Product("상품", 1000, "http://img.com", category);
        Option option = new Option(product, "옵션", 100);

        // When
        int totalPrice = option.calculateTotalPrice(5);

        // Then
        assertThat(totalPrice)
            .as("가격 1000원 * 수량 5개 = 5000원")
            .isEqualTo(5000);
    }

    @Test
    @DisplayName("calculateTotalPrice: 수량이 1일 때 상품 가격을 반환한다")
    void calculateTotalPrice_singleQuantity() {
        // Given
        Category category = new Category("카테고리", "#000", "http://img.com", "설명");
        Product product = new Product("상품", 2500, "http://img.com", category);
        Option option = new Option(product, "옵션", 100);

        // When
        int totalPrice = option.calculateTotalPrice(1);

        // Then
        assertThat(totalPrice).isEqualTo(2500);
    }

    @Test
    @DisplayName("calculateTotalPrice: 수량이 0일 때 0을 반환한다")
    void calculateTotalPrice_zeroQuantity() {
        // Given
        Category category = new Category("카테고리", "#000", "http://img.com", "설명");
        Product product = new Product("상품", 1000, "http://img.com", category);
        Option option = new Option(product, "옵션", 100);

        // When
        int totalPrice = option.calculateTotalPrice(0);

        // Then
        assertThat(totalPrice)
            .as("수량 0일 때 가격은 0이어야 한다")
            .isEqualTo(0);
    }
}
