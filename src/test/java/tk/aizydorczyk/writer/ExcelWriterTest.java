package tk.aizydorczyk.writer;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;
import tk.aizydorczyk.model.AuthorDto;
import tk.aizydorczyk.model.BookDto;
import tk.aizydorczyk.model.Header;
import tk.aizydorczyk.model.LenderDto;
import tk.aizydorczyk.util.header.HeadersCoordinatesCalculator;
import tk.aizydorczyk.util.header.HeadersInitializer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelWriterTest {

	private List<BookDto> exampleDtos;

	private List<String> listOfExpectedHeadersNames;

	final static String LENDER_TEST_FNAME_1 = "LENDER_TEST_FNAME_1";
	final static String LENDER_TEST_FNAME_2 = "LENDER_TEST_FNAME_2";
	final static String LENDER_TEST_FNAME_3 = "LENDER_TEST_FNAME_3";
	final static String LENDER_TEST_FNAME_4 = "LENDER_TEST_FNAME_4";
	final static String LENDER_TEST_FNAME_5 = "LENDER_TEST_FNAME_5";

	final static String LENDER_TEST_LNAME_1 = "LENDER_TEST_LNAME_1";
	final static String LENDER_TEST_LNAME_2 = "LENDER_TEST_LNAME_2";
	final static String LENDER_TEST_LNAME_3 = "LENDER_TEST_LNAME_3";
	final static String LENDER_TEST_LNAME_4 = "LENDER_TEST_LNAME_4";
	final static String LENDER_TEST_LNAME_5 = "LENDER_TEST_LNAME_5";

	final static String AUTHOR_TEST_FNAME_1 = "AUTHOR_TEST_FNAME_1";
	final static String AUTHOR_TEST_FNAME_2 = "AUTHOR_TEST_FNAME_2";

	final static String AUTHOR_TEST_LNAME_1 = "AUTHOR_TEST_LNAME_1";
	final static String AUTHOR_TEST_LNAME_2 = "AUTHOR_TEST_LNAME_2";

	final static String BOOK_TEST_NAME_1 = "BOOK_TEST_NAME_1";
	final static String BOOK_TEST_NAME_2 = "BOOK_TEST_NAME_2";

	@Before
	public void init(){

		LenderDto lender1 = LenderDto.builder().id(1L).firstName(LENDER_TEST_FNAME_1).lastName(LENDER_TEST_LNAME_1).build();
		LenderDto lender2 = LenderDto.builder().id(2L).firstName(LENDER_TEST_FNAME_2).lastName(LENDER_TEST_LNAME_2).build();
		AuthorDto author1 = AuthorDto.builder().id(1L).firstName(AUTHOR_TEST_FNAME_1).lastName(AUTHOR_TEST_LNAME_1).build();
		BookDto book1 = BookDto.builder().id(1L).name(BOOK_TEST_NAME_1).releaseDate(LocalDate.now()).author(author1).lenders(Arrays.asList(lender1, lender2)).build();
		LenderDto lender3 = LenderDto.builder().id(3L).firstName(LENDER_TEST_FNAME_3).lastName(LENDER_TEST_LNAME_3).build();
		LenderDto lender4 = LenderDto.builder().id(4L).firstName(LENDER_TEST_FNAME_4).lastName(LENDER_TEST_LNAME_4).build();
		LenderDto lender5 = LenderDto.builder().id(5L).firstName(LENDER_TEST_FNAME_5).lastName(LENDER_TEST_LNAME_5).build();
		AuthorDto author2 = AuthorDto.builder().id(2L).firstName(AUTHOR_TEST_FNAME_2).lastName(AUTHOR_TEST_LNAME_2).build();
		BookDto book2 = BookDto.builder().id(2L).name(BOOK_TEST_NAME_2).releaseDate(LocalDate.of(2010,12,12)).author(author2).lenders(Arrays.asList(lender3, lender4,lender5)).build();

		exampleDtos = Arrays.asList(book1,book2);

		listOfExpectedHeadersNames = Arrays.asList("Book","BOOK_ID","BOOK_NAME","RELEASE_DATE","Author","AUTHOR_ID","AUTHOR_FIRST_NAME","AUTHOR_LAST_NAME","Lender","LENDER_ID","LENDER_FIRST_NAME","LENDER_LAST_NAME");
	}

	@Test
	public void shouldInitialize12Headers() {
		HeadersInitializer headersInitializer = HeadersInitializer.ofAnnotatedObjects(exampleDtos);
		List<Header> headers = headersInitializer.initialize();

		assertEquals(12L,headers.size());
	}

	@Test
	public void shouldKeepRightOrder() {
		HeadersInitializer headersInitializer = HeadersInitializer.ofAnnotatedObjects(exampleDtos);
		List<Header> headers = headersInitializer.initialize();

		List<String> listOfHeadersNames = headers.
				stream()
				.map(Header::getHeaderName)
				.collect(Collectors.toList());

	   assertThat(listOfExpectedHeadersNames,is(listOfHeadersNames));
	}

	@Test
	public void shouldCalculateHeadersCoordinates() {
		HeadersInitializer headersInitializer = HeadersInitializer.ofAnnotatedObjects(exampleDtos);
		List<Header> headers = headersInitializer.initialize();
		HeadersCoordinatesCalculator headersCoordinatesCalculator = new HeadersCoordinatesCalculator();

		List<Header> calculate = headersCoordinatesCalculator.calculate(headers);

		Header book = calculate.get(0);
		Header bookId = calculate.get(1);
		Header bookName = calculate.get(2);
		Header releaseDate = calculate.get(3);
		Header author = calculate.get(4);
		Header authorId = calculate.get(5);
		Header authorFirstName = calculate.get(6);
		Header authorLastName = calculate.get(7);
		Header lender = calculate.get(8);
		Header lenderId = calculate.get(9);
		Header lenderFirstName = calculate.get(10);
		Header lenderLastName = calculate.get(11);

		assertEquals(Long.valueOf(0), book.getRowPosition());
		assertEquals(Long.valueOf(0), book.getStartColumnPosition());
		assertEquals(Long.valueOf(8), book.getEndColumnPosition());

		assertEquals(Long.valueOf(1), bookId.getRowPosition());
		assertEquals(Long.valueOf(0), bookId.getStartColumnPosition());
		assertEquals(Long.valueOf(0), bookId.getEndColumnPosition());

		assertEquals(Long.valueOf(1), bookName.getRowPosition());
		assertEquals(Long.valueOf(1), bookName.getStartColumnPosition());
		assertEquals(Long.valueOf(1), bookName.getEndColumnPosition());

		assertEquals(Long.valueOf(1), releaseDate.getRowPosition());
		assertEquals(Long.valueOf(2), releaseDate.getStartColumnPosition());
		assertEquals(Long.valueOf(2), releaseDate.getEndColumnPosition());

		assertEquals(Long.valueOf(1), author.getRowPosition());
		assertEquals(Long.valueOf(3), author.getStartColumnPosition());
		assertEquals(Long.valueOf(6), author.getEndColumnPosition());

		assertEquals(Long.valueOf(2), authorId.getRowPosition());
		assertEquals(Long.valueOf(3), authorId.getStartColumnPosition());
		assertEquals(Long.valueOf(3), authorId.getEndColumnPosition());

		assertEquals(Long.valueOf(2), authorFirstName.getRowPosition());
		assertEquals(Long.valueOf(4), authorFirstName.getStartColumnPosition());
		assertEquals(Long.valueOf(4), authorFirstName.getEndColumnPosition());

		assertEquals(Long.valueOf(2), authorLastName.getRowPosition());
		assertEquals(Long.valueOf(5), authorLastName.getStartColumnPosition());
		assertEquals(Long.valueOf(5), authorLastName.getEndColumnPosition());

		assertEquals(Long.valueOf(1), lender.getRowPosition());
		assertEquals(Long.valueOf(6), lender.getStartColumnPosition());
		assertEquals(Long.valueOf(9), lender.getEndColumnPosition());

		assertEquals(Long.valueOf(2), lenderId.getRowPosition());
		assertEquals(Long.valueOf(6), lenderId.getStartColumnPosition());
		assertEquals(Long.valueOf(6), lenderId.getEndColumnPosition());

		assertEquals(Long.valueOf(2), lenderFirstName.getRowPosition());
		assertEquals(Long.valueOf(7), lenderFirstName.getStartColumnPosition());
		assertEquals(Long.valueOf(7), lenderFirstName.getEndColumnPosition());

		assertEquals(Long.valueOf(2), lenderLastName.getRowPosition());
		assertEquals(Long.valueOf(8), lenderLastName.getStartColumnPosition());
		assertEquals(Long.valueOf(8), lenderLastName.getEndColumnPosition());
	}

}
