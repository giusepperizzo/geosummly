package it.unito.geosummly;

import java.util.Arrays;
import fi.foyt.foursquare.api.entities.Category;


/**
 * @author Giacomo Falcone
 *
 * This class represents a template of the object returned by 4square
 */

public class FoursquareDataObject {
	private int row;
	private int column;
	private String venueId;
	private String venueName;
	private Double latitude;
	private Double longitude;
	private Category[] categories;
	private String email;
	private String phone;
	private String facebook;
	private String twitter;
	private boolean verified;
	private Integer checkinsCount;
	private Integer usersCount;
	private String url;
	private Long hereNow;
	

	public FoursquareDataObject(){}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getVenueId() {
		return venueId;
	}

	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public Integer getCheckins_count() {
		return checkinsCount;
	}

	public void setCheckinsCount(Integer checkinsCount) {
		this.checkinsCount = checkinsCount;
	}

	public Integer getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(Integer usersCount) {
		this.usersCount = usersCount;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getHereNow() {
		return hereNow;
	}

	public void setHereNow(Long hereNow) {
		this.hereNow = hereNow;
	}
	
	public String toString() {
		return "FoursquareDataObject [row=" + row + ", column=" + column
				+ ", venueId=" + venueId + ", venueName=" + venueName
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", categories=" + Arrays.toString(categories) + ", email="
				+ email + ", phone=" + phone + ", facebook=" + facebook
				+ ", twitter=" + twitter + ", verified=" + verified
				+ ", checkinsCount=" + checkinsCount + ", usersCount="
				+ usersCount + ", url=" + url + ", hereNow=" + hereNow + "]";
	}
}
