package com.cbi.ccr.csw.dashboard.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cbi.ccr.csw.dashboard.I18nDashboard;
import com.cbi.ccr.csw.dashboard.jwt.JwtProvider;
import com.cbi.ccr.csw.dashboard.jwt.JwtResponse;
import com.cbi.ccr.csw.dashboard.jwt.TokenRefresh;
import com.cbi.ccr.csw.dashboard.user.dto.RoleName;
import com.cbi.ccr.csw.dashboard.user.dto.SigninRequestDTO;
import com.cbi.ccr.csw.dashboard.user.dto.UserFilterDTO;
import com.cbi.ccr.csw.domain.user.UserRepository;
import com.cbi.ccr.csw.domain.user.Users;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.exception.AccessDeniedException;
import com.cbi.frw.common.exception.ChcException;

@Service
public class UserService {

	private static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.:;\\-_#@$!%*?&])[A-Za-z0-9\\d.:;\\-_#@$!%*?&]{8,64}$";
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtProvider tokenProvider;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	protected ModelMapper mapper;

	public JwtResponse authenticateUser(SigninRequestDTO signinRequestDTO) {

		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					signinRequestDTO.getUsername(), signinRequestDTO.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = tokenProvider.generateTokenFromUserName(signinRequestDTO.getUsername());
			String role = null;
			Long userId;

			if (authentication != null) {
				Users user = (Users) authentication.getPrincipal();
				role = user.getRoles();
				userId = user.getId();

				return new JwtResponse(userId, jwt, role);
			} else {
				throw new AccessDeniedException();
			}

		} catch (AuthenticationException e) {
			throw new AccessDeniedException();
		}
	}

	public TokenRefresh refreshJwtToken(String username) {
		return new TokenRefresh(tokenProvider.generateTokenFromUserName(username), "Bearer", null);
	}

	@Transactional
	public void create(UserDTO dto) throws ChcException {

		if (Boolean.TRUE.equals(userRepository.existsByUsername(dto.getUsername()))) {
			throw new ChcException(I18nDashboard.ERR_USER_ALREADY_TAKEN);
		}

		Users user = new Users();
		mapToEntity(dto, user);

		userRepository.save(user);
	}

	public void update(UserDTO dto) throws ChcException {

		Optional<Users> user = Optional.ofNullable(userRepository.findById(dto.getId())
				.orElseThrow(() -> new ChcException(I18nDashboard.ERR_USER_NOT_FOUND)));
		if (user.isPresent()) {
			user.get().setUsername(dto.getUsername());
			user.get().setFullName(dto.getFullName());
			user.get().setRoles(buildRoles(dto.getRoles()));
		    user.get().setSecretAnswerOne(dto.getSecretAnswerOne());
		    user.get().setSecretAnswerTwo(dto.getSecretAnswerTwo());
		    user.get().setSecretResponseOne(dto.getSecretResponseOne());
		    user.get().setSecretResponseTwo(dto.getSecretResponseTwo());
//			mapToEntity(dto, user.get());
			userRepository.save(user.get());
		}
	}
	
	public void changePassword(UserDTO dto) throws ChcException {

		Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(dto.getUsername())
				.orElseThrow(() -> new ChcException(I18nDashboard.ERR_USER_NOT_FOUND)));

		if (!encoder.matches(dto.getCurrentPassword(), user.get().getPassword())) {
			throw new ChcException(I18nDashboard.INVALID_CURRENT_PASSWORD);
		} else {
			validatePassword(dto.getPassword());
			user.get().setPassword(encoder.encode(dto.getPassword()));
			userRepository.save(user.get());

		}
	}
	
	
	public void resetPassword(UserDTO dto) throws ChcException {

		Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(dto.getUsername())
				.orElseThrow(() -> new ChcException(I18nDashboard.ERR_USER_NOT_FOUND)));

		if (dto.getSecretResponseOne().equals(user.get().getSecretResponseOne())  && dto.getSecretResponseTwo().equals(user.get().getSecretResponseTwo()) ) {
			validatePassword(dto.getPassword());
			user.get().setPassword(encoder.encode(dto.getPassword()));
			userRepository.save(user.get());
		} else {
			throw new ChcException(I18nDashboard.INVALID_SECRET_DETAILS);

		}
	}
	
	private void validatePassword(String password) throws ChcException {
		if(!password.matches(REGEX_PASSWORD)) {
			throw new ChcException(I18nDashboard.INVALID_SECRET_DETAILS);
		}
	}

	private void mapToEntity(UserDTO dto, Users user) throws ChcException {
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setFullName(dto.getFullName());
		validatePassword(dto.getPassword());
		user.setPassword(encoder.encode(dto.getPassword()));
		user.setRoles(buildRoles(dto.getRoles()));
		user.setSecretAnswerOne(dto.getSecretAnswerOne());
		user.setSecretAnswerTwo(dto.getSecretAnswerTwo());
		user.setSecretResponseOne(dto.getSecretResponseOne());
		user.setSecretResponseTwo(dto.getSecretResponseTwo());
	}

	private void mapToDTO(UserDTO dto, Users user) {
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setFullName(user.getFullName());
		dto.setPassword(user.getPassword());
		dto.setRoles(buildRoles(user.getRoles()));
		dto.setSecretAnswerOne(user.getSecretAnswerOne());
		dto.setSecretAnswerTwo(user.getSecretAnswerTwo());
		dto.setSecretResponseOne(user.getSecretResponseOne());
		dto.setSecretResponseTwo(user.getSecretResponseTwo());
	}

	private Set<RoleName> buildRoles(String roles) {
		String role = roles.replace("[", "").replace("]", "");
		String[] split = role.split(",");
		return Arrays.stream(split).map(RoleName::valueOf).collect(Collectors.toSet());

	}

	private String buildRoles(Set<RoleName> roles) {
		StringBuilder sb = new StringBuilder();
		roles.forEach(role -> {
			sb.append(role);
			sb.append(",");
		});
		return sb.toString();
	}

	@Transactional
	public void delete(Long id) throws ChcException {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new ChcException(I18nDashboard.ERR_USER_NOT_FOUND);
		}
	}

	public PagedResultDTO<UserDTO> list(UserFilterDTO filterDTO) {

		Users users = BeanUtils.instantiateClass(Users.class);
		mapper.map(filterDTO, users);

		Page<Users> list = userRepository.findAll(Example.of(users, ExampleMatcher.matchingAll()),
				Pageable.ofSize(filterDTO.getMaxRow()).withPage(filterDTO.getOffset()));

		List<UserDTO> dtos = new ArrayList<>(list.getSize());
		list.forEach(c -> {
			UserDTO dto = BeanUtils.instantiateClass(UserDTO.class);
			mapToDTO(dto, c);
			dtos.add(dto);
		});
		return new PagedResultDTO<>(list.getTotalPages(), list.getTotalElements(), dtos);
	}
	
	public UserDTO getSecretDetails(UserDTO dto) throws ChcException {

		Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(dto.getUsername())
				.orElseThrow(() -> new ChcException(I18nDashboard.ERR_USER_NOT_FOUND)));

		if (user.isPresent()) {
			dto.setSecretAnswerOne(user.get().getSecretAnswerOne());
			dto.setSecretAnswerTwo(user.get().getSecretAnswerTwo());
		}

		return dto;
	}

}
