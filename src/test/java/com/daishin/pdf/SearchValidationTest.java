package com.daishin.pdf;

import com.daishin.pdf.page.ErrorSearch;
import com.daishin.pdf.page.Page;
import com.daishin.pdf.page.Search;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchValidationTest {
    @Test
    void acceptsDefaultsAndRejectsSqlFragmentsAndInvalidPageSizes() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new Search()).isEmpty());
            assertTrue(validator.validate(new ErrorSearch()).isEmpty());
            Search search = new Search();
            search.setCate("MASTER_KEY; DROP TABLE MASTER");
            search.setPageElement(0);
            assertEquals(2, validator.validate(search).size());
            ErrorSearch errors = new ErrorSearch();
            errors.setSort("DESC;--");
            errors.setSortCate("ID");
            assertEquals(2, validator.validate(errors).size());
        }
    }

    @Test
    void keepsOffsetAndEmptyResultsNonNegative() {
        Search search = new Search();
        search.setPageElement(2);
        assertEquals(2, new Page(2, 5, search).getStartNum());
        assertEquals(0, new Page(1, 0, search).getStartNum());
        ErrorSearch errors = new ErrorSearch();
        errors.setPageElement(2);
        assertEquals(2, new Page(2, 5, errors).getStartNum());
    }
}
