package com.cbi.ccr.csw.dashboard.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleAdmin;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.dashboard.user.dto.UserFilterDTO;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

@RestController
@RequestMapping(ControllerPath.USER_PREFIX)
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	protected ModelMapper mapper;

	@PreAuthorizeRoleAdmin
	@PostMapping(ControllerPath.USER_LIST)
	public ResponseEntity<PagedResultDTO<UserDTO> > list(@RequestBody UserFilterDTO filter){
		PagedResultDTO<UserDTO> list = userService.list(filter);
		return ResponseEntity.ok(list);
	}
	
	@PostMapping
	@PreAuthorizeRoleAdmin
	public ResponseEntity<Void> createUser(@RequestBody UserDTO dto) throws ChcException {

		userService.create(dto);
		return ResponseEntity.ok().build();

	}
	
	@PostMapping(ControllerPath.USER_DELETE+"/{id}")
	@PreAuthorizeRoleAdmin
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws ChcException {
		
		userService.delete(id);
		return ResponseEntity.ok().build(); 
	}

	@PostMapping(ControllerPath.USER_UPDATE)
	@PreAuthorizeRoleAdmin
	public ResponseEntity<Void> updateUser(@RequestBody UserDTO dto) throws ChcException {

		userService.update(dto);
		
		return ResponseEntity.ok().build();

	}
	
	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.CHANGE_PASSWORD)
	public ResponseEntity<Void> chnagePassword(@RequestBody UserDTO dto) throws ChcException {

		userService.changePassword(dto);
		
		return ResponseEntity.ok().build();

	}
	// visualizza de Sequre questions, deve essere publico
	@PostMapping(ControllerPath.SECRET_DETAILS)
	public ResponseEntity<UserDTO> secretDetails(@RequestBody UserDTO dto)  throws ChcException{
		UserDTO user = userService.getSecretDetails(dto);
		return ResponseEntity.ok(user);
	}

	// esegure rest password, deve essere publico
	@PostMapping(ControllerPath.RESET_PASSWORD)
	public ResponseEntity<Void> resetPassword(@RequestBody UserDTO dto) throws ChcException {

		userService.resetPassword(dto);
		
		return ResponseEntity.ok().build();

	}
	
}
