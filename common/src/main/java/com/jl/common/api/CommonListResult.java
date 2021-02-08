package com.jl.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.IErrorCode;

import java.util.List;

/**
 * 通用返回对象
 *
 * @author JinLongXu
 */

public class CommonListResult<T> extends CommonResult<T>{
	private long code;
	private String message;
	private List<T> data;
	private Long size;
	private Long count;

	protected CommonListResult() {
	}


	protected CommonListResult(long code, String message) {
		this.code = code;
		this.message = message;
	}

	protected CommonListResult(long code, String message, List<T> data) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.size = (long) data.size();
		this.count = (long) data.size();
	}

	protected CommonListResult(long code, String message, List<T> data, Long size, Long count) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.size = size;
		this.count = count;
	}


	public static <T> CommonListResult<T> success(String message) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), message);
	}

	public static <T> CommonListResult<T> successList(List<T> data) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
	}

	public static <T> CommonListResult<T> successList(List<T> data, Long total) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, (long) data.size(), total);
	}


	/**
	 * 成功返回结果
	 *
	 * @param data    获取的数据
	 * @param message 提示信息
	 */
	public static <T> CommonListResult<T> successList(List<T> data, String message) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), message, data);
	}

	/**
	 * 成功返回结果
	 *
	 * @param data    获取的数据
	 * @param message 提示信息
	 */
	public static <T> CommonListResult<T> successList(List<T> data, Long total, String message) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), message, data, (long) data.size(), (long) total);
	}


	public static <T> CommonListResult<T> successIPage(IPage<T> data) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data.getRecords(), data.getSize(), data.getTotal());
	}


	/**
	 * 成功返回结果
	 *
	 * @param data    获取的数据
	 * @param message 提示信息
	 */
	public static <T> CommonListResult<T> successIPage(IPage<T> data, String message) {
		return new CommonListResult<T>(ResultCode.SUCCESS.getCode(), message, data.getRecords(), data.getSize(), data.getTotal());
	}


	/**
	 * 失败返回结果
	 *
	 * @param errorCode 错误码
	 */
	public static <T> CommonListResult<T> failed(IErrorCode errorCode) {
		return new CommonListResult<T>(errorCode.getCode(), errorCode.getMsg());
	}


	/**
	 * 失败返回结果
	 *
	 * @param message 提示信息
	 */
	public static <T> CommonListResult<T> failed(String message) {
		return new CommonListResult<T>(ResultCode.FAILED.getCode(), message);
	}

	/**
	 * 失败返回结果
	 */
	public static <T> CommonListResult<T> failed() {
		return failed(ResultCode.FAILED);
	}

	/**
	 * 参数验证失败返回结果
	 */
	public static <T> CommonListResult<T> validateFailed() {
		return failed(ResultCode.VALIDATE_FAILED);
	}

	/**
	 * 参数验证失败返回结果
	 *
	 * @param message 提示信息
	 */
	public static <T> CommonListResult<T> validateFailed(String message) {
		return new CommonListResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
	}

	/**
	 * 未登录返回结果
	 */
	public static <T> CommonListResult<T> unauthorized(List<T> data) {
		return new CommonListResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
	}

	/**
	 * 未授权返回结果
	 */
	public static <T> CommonListResult<T> forbidden(List<T> data) {
		return new CommonListResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
	}

	/**
	 * 未授权返回结果
	 */
	public static <T> CommonListResult<T> forbidden() {
		return new CommonListResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
	}

	@Override
	public long getCode() {
		return code;
	}


	@Override
	public void setCode(long code) {
		this.code = code;
	}


	@Override
	public String getMessage() {
		return message;
	}


	@Override
	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public T getData() {
		return (T) data;
	}

	public void setData(List<T> data) {
		this.data = data;


	}

	public Long getSize() {
		return size;
	}

	public Long getCount() {
		return count;
	}
}
