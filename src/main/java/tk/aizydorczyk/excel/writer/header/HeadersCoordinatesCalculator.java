package tk.aizydorczyk.excel.writer.header;

import lombok.Getter;
import tk.aizydorczyk.excel.common.enums.Messages;
import tk.aizydorczyk.excel.common.model.Header;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static tk.aizydorczyk.excel.common.enums.Messages.NO_BOTTOM_HEADERS;
import static tk.aizydorczyk.excel.common.enums.Messages.NO_DATA_HEADERS;
import static tk.aizydorczyk.excel.common.enums.Messages.NO_MAIN_HEADER;
import static tk.aizydorczyk.excel.common.utils.ParserUtils.notSetBefore;
import static tk.aizydorczyk.excel.common.utils.ParserUtils.selectMainHeaderOrThrow;

@Getter
public class HeadersCoordinatesCalculator {

	private final List<Header> calculatedHeaders;
	private final int firstDataRowPosition;

	public HeadersCoordinatesCalculator(List<Header> headers) {
		this.calculatedHeaders = calculate(headers);
		this.firstDataRowPosition = getFirstDataRowPosition(headers);
	}

	private List<Header> calculate(List<Header> headers) {
		calculateColumnPositions(headers);

		final Header mainHeader = selectMainHeaderOrThrow(headers, () ->
				new CoordinatesCalculateFail(NO_MAIN_HEADER));

		calculateBottomRowsPosition(mainHeader);
		calculateMainHeaderColumnPosition(mainHeader);

		alignDataHeadersRowPosition(headers);
		return headers;
	}

	private void calculateColumnPositions(List<Header> headers) {
		calculateColumnPositionsOfDataHeaders(headers);
		calculateColumnPositionsOfRestHeadersByDataHeadersPositions(headers);
	}

	@SuppressWarnings("ConstantConditions")
	private void alignDataHeadersRowPosition(List<Header> headers) {
		final OptionalInt maxRowPosition = headers
				.stream()
				.filter(Header::isOverData)
				.mapToInt(Header::getRowPosition)
				.max();
		headers.stream()
				.filter(Header::isOverData)
				.forEach(header ->
						header.setRowPosition(maxRowPosition.getAsInt()));
	}

	private void calculateColumnPositionsOfDataHeaders(List<Header> headers) {
		final List<Header> dataHeaders = headers
				.stream()
				.filter(Header::isOverData)
				.collect(Collectors.toList());

		int columnIndex = 0;
		for (final Header dataHeader : dataHeaders) {
			dataHeader.setStartColumnPosition(columnIndex);
			dataHeader.setEndColumnPosition(columnIndex);
			columnIndex++;
		}
	}

	private void calculateColumnPositionsOfRestHeadersByDataHeadersPositions(List<Header> headers) {
		final List<Header> notOverDataHeaders = headers.stream()
				.filter(Header::notOverData)
				.collect(Collectors.toList());

		calculateStartAndEndColumnPositions(notOverDataHeaders);
	}

	private void calculateStartAndEndColumnPositions(List<Header> notOverDataHeaders) {
		for (final Header header : notOverDataHeaders) {
			if (notSetBefore(header.getStartColumnPosition())) {
				calculateStartColumnPositions(header);
			}
			calculateEndColumnPosition(header);
		}
	}

	private void calculateStartColumnPositions(Header header) {
		final Header firstBottomHeader = header.getBottomHeaders().stream()
				.findFirst()
				.orElseThrow(() ->
						new CoordinatesCalculateFail(NO_BOTTOM_HEADERS));

		if (notSetBefore(firstBottomHeader.getStartColumnPosition())) {
			calculateStartColumnPositions(firstBottomHeader);
			header.setStartColumnPosition(firstBottomHeader.getStartColumnPosition());
		} else {
			header.setStartColumnPosition(firstBottomHeader.getStartColumnPosition());
		}
	}

	private void calculateEndColumnPosition(Header header) {
		final int endColumnPosition = header.getStartColumnPosition() + header.getWidth() - 1;
		header.setEndColumnPosition(endColumnPosition);
	}

	private void calculateMainHeaderColumnPosition(Header mainHeader) {
		final int startColumnPosition = mainHeader.getStartColumnPosition();
		final int endColumnPosition = startColumnPosition + mainHeader.getWidth() - 1;
		mainHeader.setEndColumnPosition(endColumnPosition);
	}

	private void calculateBottomRowsPosition(Header header) {
		for (final Header bottomHeader : header.getBottomHeaders()) {
			if (notSetBefore(header.getRowPosition())) {
				header.setRowPosition(0);
			}

			bottomHeader.setRowPosition(header.getRowPosition() + 1);
			calculateBottomRowsPosition(bottomHeader);
		}
	}

	private int getFirstDataRowPosition(List<Header> headers) {
		return headers.stream()
				.filter(Header::isOverData)
				.findAny()
				.map(Header::getRowPosition)
				.orElseThrow(() ->
						new CoordinatesCalculateFail(NO_DATA_HEADERS));
	}

	private class CoordinatesCalculateFail extends RuntimeException {
		public CoordinatesCalculateFail(Messages message) {
			super(message.getMessage());
		}
	}


}