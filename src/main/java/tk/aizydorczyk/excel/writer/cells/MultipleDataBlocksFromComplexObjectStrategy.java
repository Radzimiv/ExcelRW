package tk.aizydorczyk.excel.writer.cells;

import tk.aizydorczyk.excel.common.model.DataBlock;
import tk.aizydorczyk.excel.common.model.DataCell;
import tk.aizydorczyk.excel.common.model.Header;
import tk.aizydorczyk.excel.writer.cells.DataBlockCreator.DataBlockCreateFail;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static tk.aizydorczyk.excel.common.messages.Messages.CANNOT_CAST_TO_COLLECTION;
import static tk.aizydorczyk.excel.common.messages.Messages.CANNOT_GET_VALUE_FROM_FIELD;
import static tk.aizydorczyk.excel.common.utility.WriterHelper.getObjectFromFieldOrThrow;
import static tk.aizydorczyk.excel.common.utility.WriterHelper.isCollection;

final class MultipleDataBlocksFromComplexObjectStrategy implements BlocksCreationStrategy {

	@Override
	public List<DataBlock> createBlocks(BlocksCreationDto creationDto, BiFunction<Object, List<Header>, DataBlock> blockCreationFunction) {
		final Optional<Object> optional = getObjectFromFieldOrThrow(creationDto.field, creationDto.untypedObject, () ->
				new DataBlockCreateFail(CANNOT_GET_VALUE_FROM_FIELD));

		return optional.map(collection ->
				mapToInternalDataBlocks(creationDto, blockCreationFunction, collection))
				.orElseGet(() ->
						getDataBlockListWithOneBlockWithOneCellWithoutData(creationDto.header));
	}

	private List<DataBlock> mapToInternalDataBlocks(BlocksCreationDto creationDto, BiFunction<Object, List<Header>, DataBlock> blockCreationFunction, Object uncastCollection) {
		if (isCollection(uncastCollection.getClass())) {
			final Collection<Object> collection = (Collection<Object>) uncastCollection;

			return collection.stream()
					.map(data ->
							blockCreationFunction.apply(data, creationDto.header.getBottomHeaders()))
					.collect(Collectors.toList());
		} else {
			throw new DataBlockCreateFail(CANNOT_CAST_TO_COLLECTION);
		}
	}

	private List<DataBlock> getDataBlockListWithOneBlockWithOneCellWithoutData(Header header) {
		return Collections.singletonList(DataBlock.createWithHeaderAndCells(
				header, Collections.singletonList(DataCell.createWithNotCastData(null))
		));
	}
}