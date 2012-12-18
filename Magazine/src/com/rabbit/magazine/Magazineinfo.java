package com.rabbit.magazine;

import java.io.Serializable;

/**
 * 杂志数据
 * 
 * @author litingwen
 * 
 */
public class Magazineinfo implements Serializable {

	private static final long serialVersionUID = -5365882850646568504L;

	private int id;

	private String coverImage;
	private String coverImgPath;
	private String zipUrl;
	private String previewZipUrl;
	private String bytes;
	private String pageNumber;
	private String title;
	private String description;
	private Double iosPrice;
	private String productId;
	private String updatetick;
	private String previewImage;
	private String[] previewImageTest;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getZipUrl() {
		return zipUrl;
	}

	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}

	public String getPreviewZipUrl() {
		return previewZipUrl;
	}

	public void setPreviewZipUrl(String previewZipUrl) {
		this.previewZipUrl = previewZipUrl;
	}

	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getIosPrice() {
		return iosPrice;
	}

	public void setIosPrice(Double iosPrice) {
		this.iosPrice = iosPrice;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getUpdatetick() {
		return updatetick;
	}

	public void setUpdatetick(String updatetick) {
		this.updatetick = updatetick;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}

	public String[] getPreviewImageTest() {
		return previewImageTest;
	}

	public void setPreviewImageTest(String[] previewImageTest) {
		this.previewImageTest = previewImageTest;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCoverImgPath() {
		return coverImgPath;
	}

	public void setCoverImgPath(String coverImgPath) {
		this.coverImgPath = coverImgPath;
	}

}
