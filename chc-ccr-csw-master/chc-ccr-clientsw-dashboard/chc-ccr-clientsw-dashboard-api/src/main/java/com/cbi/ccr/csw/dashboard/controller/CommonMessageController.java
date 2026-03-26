package com.cbi.ccr.csw.dashboard.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;
import com.cbi.frw.api.dto.GenericDTO;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.util.DynamicFieldUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CommonMessageController<D extends GenericDTO, F extends PageableFilterDTO, R extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>, E> {

	@Autowired
	protected ModelMapper mapper;

	@Autowired
	protected R repo;

	@NonNull
	private Class<D> dtoClass;
	@NonNull
	@Getter
	private Class<E> entityClass;

	public Specification<E> getSpecRangeAndExample(F filterDTO, Example<E> example) {

		return (Specification<E>) (root, query, builder) -> {
			final List<Predicate> predicates = new ArrayList<>();
			
			dateRangeFilter(root, builder,predicates,filterDTO.getStartDate(),filterDTO.getEndDate(), filterDTO.getFromTime(), filterDTO.getToTime(), filterDTO.getDateColumnName());
		
			logicalStateFilter(root, builder,predicates,filterDTO);

			fileSizeFilter(root, builder,predicates, filterDTO.getFileSizeList());
			
			msgSizeFilter(root, builder,predicates, filterDTO.getMsgSizeList());


			predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	
	
	private void dateRangeFilter(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates, String startDate,
		String endDate, String startTime, String endTime, String columnName) {
	
		LocalTime startLocalTime = LocalTime.parse("00:00:00");
		LocalTime endLocalTime = LocalTime.parse("23:59:59");

		if(startDate!=null) {
			LocalDate startDateObj = LocalDate.parse(startDate.split("T")[0]).plusDays(1);
			if(startTime!=null) {
				startLocalTime = LocalTime.parse(startTime);
			}
			LocalDateTime startDateTime =  LocalDateTime.of(startDateObj,startLocalTime); 
			predicates.add(builder.greaterThanOrEqualTo(root.get(columnName),startDateTime));
		}
	
		if(endDate!=null) {
			LocalDate endDateObj = LocalDate.parse(endDate.split("T")[0]).plusDays(1);
			if(endTime!=null) {
				endLocalTime = LocalTime.parse(endTime);
			}
			LocalDateTime endDateTime =  LocalDateTime.of(endDateObj,endLocalTime); 
			predicates.add(builder.lessThanOrEqualTo(root.get(columnName),endDateTime));
		}
	}
	
	
	
	
	
	private void fileSizeFilter(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates,
			List<Long> fileSizeList) {

		if (fileSizeList != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("fileSize"), fileSizeList.get(0)));
			predicates.add(builder.lessThanOrEqualTo(root.get("fileSize"), fileSizeList.get(1)));
		}
	}
	
	private void msgSizeFilter(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates,
			List<Integer> msgSizeList) {

		if (msgSizeList != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("messageLeng"), msgSizeList.get(0)));
			predicates.add(builder.lessThanOrEqualTo(root.get("messageLeng"), msgSizeList.get(1)));
		}
	}
	
	
	private void logicalStateFilter(Root<E> root, CriteriaBuilder builder, List<Predicate> predicates,
			F filterDTO) {
		if (filterDTO.getLogicalStateEnumValueList() != null) {
			predicates.add(builder.in(root.get(filterDTO.getStatusColumnName()))
					.value(filterDTO.getLogicalStateEnumValueList()));
		}
	}
	

	protected PagedResultDTO<D> getPageableEntity(F filterDTO) {
		E entity = BeanUtils.instantiateClass(entityClass);
		DynamicFieldUtils.setAllNull(entity);
		mapper.map(filterDTO, entity);
		Page<E> list;
		ExampleMatcher customExampleMatcher = buildExampleMatcher();
		// Page<E> list = repo.findAll(Example.of(entity,customExampleMatcher),Pageable.ofSize(filterDTO.getMaxRow()).withPage(filterDTO.getOffset()));

		if (filterDTO.getDateColumnName() != null) {

			Pageable paging = PageRequest.of(filterDTO.getOffset(), filterDTO.getMaxRow(),
					Sort.by(Sort.Direction.DESC, filterDTO.getDateColumnName()));

			list = repo.findAll(getSpecRangeAndExample(filterDTO, Example.of(entity, customExampleMatcher)), paging);
		} else if (filterDTO.isOrderBylocaRemoteBa()) {
			Pageable paging = PageRequest.of(filterDTO.getOffset(), filterDTO.getMaxRow(),
					Sort.by(Sort.Direction.DESC, "localBaId", "remoteBaId"));

			list = repo.findAll(getSpecRangeAndExample(filterDTO, Example.of(entity, customExampleMatcher)), paging);
			
		} else {
			
			list = repo.findAll(getSpecRangeAndExample(filterDTO, Example.of(entity, customExampleMatcher)),
					Pageable.ofSize(filterDTO.getMaxRow()).withPage(filterDTO.getOffset()));
			
		}

		List<D> dtos = new ArrayList<>(list.getSize());
		list.forEach(c -> {
			D dto = BeanUtils.instantiateClass(dtoClass);
			mapper.map(c, dto);
			dtos.add(dto);
		});
		return new PagedResultDTO<D>(list.getTotalPages(), list.getTotalElements(), dtos);
	}

	protected ExampleMatcher buildExampleMatcher() {
		return ExampleMatcher.matchingAll();
	}
}
