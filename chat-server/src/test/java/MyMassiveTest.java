import homeworks.MyMassive;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class MyMassiveTest {

    private MyMassive mass;
    int[] numbers = {5, 6, 7, 1, 8};
    int[] numbersTwo = {1, 6, 7, 4, 8};
    int[] bullets = {1, 4, 7, 9, 4, 6, 7};
    int[] bulletsTwo = {1, 4, 7, 9, 4, 6, 4};

    @BeforeEach
    void init() {
        mass = new MyMassive();
    }

   @Test
    void changeMassive() {
        int[] eq = {6, 7};
        int[] eqTwo = {};
        Assertions.assertArrayEquals(eq, mass.changeMassive(bullets));
       Assertions.assertArrayEquals(eqTwo, mass.changeMassive(bulletsTwo));
    }

    @Test
        void searchMassive() {
        Assertions.assertFalse(mass.searchMassive(numbers));
        Assertions.assertTrue(mass.searchMassive(numbersTwo));
    }

    @CsvSource({
            "1 4 6 8 7 4",
            "2 4 5 7 8 4",
            "2 4 7 9 1 7",
            "4 5 6 9 4 6"
    }
    )
    @ParameterizedTest
    void searchMassiveElse(int[] param) {
        Assertions.assertFalse(mass.searchMassive(param));
    }


}
