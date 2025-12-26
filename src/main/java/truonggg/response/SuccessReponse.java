package truonggg.response;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuccessReponse<T> {
	private final OperationType OPERATION_TYPE = OperationType.Success;
	private final String message = "success";
	private ErrorCode code;
	private T data;
	private int size;
	// private final OffsetDateTime timestamp = OffsetDateTime.now();
	@JsonFormat(timezone = "Asia/Saigon", pattern = "dd/MM/yyyy hh:mm:ss")
	@JsonProperty(value = "thoi gian")
	private final Date timestamp = new Date();
	private int page;
	private Long totalElements;
	private Integer totalPages;

	public static <T> SuccessReponse<T> of(final T data) {
		return SuccessReponse.<T>builder().data(data).code(ErrorCode.OK).size(getSize(data)).build();
	}

	public static <T> SuccessReponse<T> of(final T data, final int page) {
		return SuccessReponse.<T>builder().data(data).code(ErrorCode.OK).size(getSize(data)).page(page).build();
	}

	public static <T> SuccessReponse<List<T>> ofPage(final Page<T> page) {
		return SuccessReponse.<List<T>>builder()
				.data(page.getContent())
				.code(ErrorCode.OK)
				.size(page.getNumberOfElements())
				.page(page.getNumber())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.build();
	}

	private static <T> int getSize(final T data) {
		if (Objects.nonNull(data) && data instanceof Collection<?>) {
			return ((Collection<?>) data).size();
		}
		return 0;
	}
}
