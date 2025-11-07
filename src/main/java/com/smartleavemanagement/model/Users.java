package com.smartleavemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.smartleavemanagement.enums.Gender;
import com.smartleavemanagement.enums.OtpStatus;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Phone number is required")
    @Min(value = 1000000000L, message = "Phone number must be at least 10 digits")
    private Long phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;
    
    private String userRole;
    
    

    public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = role.getRoleName();
	}


	private int otp;

    @Enumerated(EnumType.STRING)
    private OtpStatus otpStatus = OtpStatus.GENERATE;
    
  
    
    
    @NotBlank(message="Country name is required")
    private String countryName;

    

	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Users() {
        this.role = new Roles();
        
    }
    
    
    

	public Users(int userId, @NotBlank(message = "First name is required") String firstName,
			@NotBlank(message = "Last name is required") String lastName,
			@Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
			@NotNull(message = "Phone number is required") @Min(value = 1000000000, message = "Phone number must be at least 10 digits") Long phoneNumber,
			@NotBlank(message = "Address is required") String address,
			@NotNull(message = "Gender is required") Gender gender,
			@NotBlank(message = "Username is required") String userName,
			@NotBlank(message = "Password is required") String password,
			@NotBlank(message = "Country name is required") String countryName) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.gender = gender;
		this.userName = userName;
		this.password = password;
		this.countryName=countryName;
		
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public OtpStatus getOtpStatus() {
		return otpStatus;
	}

	public void setOtpStatus(OtpStatus otpStatus) {
		this.otpStatus = otpStatus;
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", address=" + address + ", gender=" + gender + ", userName="
				+ userName + ", password=" + password + ", role=" + role + ", userRole=" + userRole + ", otp=" + otp
				+ ", otpStatus=" + otpStatus + ", countryName=" + countryName + "]";
	}
	
	
	
	
	

}
