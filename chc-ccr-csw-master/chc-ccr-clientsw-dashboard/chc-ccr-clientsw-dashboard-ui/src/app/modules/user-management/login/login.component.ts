import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../_services/auth.service';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { TokenStorageService } from '../../_services/token-storage.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm = new FormGroup({});
  username!: string;
  password!: string;
 
  constructor(private authService: AuthService, private tokenStorageService: TokenStorageService, private route: Router) { }

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm() {
    this.loginForm = new FormGroup({
      username: new FormControl(this.username, [Validators.required,]),
      password: new FormControl(this.password, [Validators.required, ]),
        
    });
}

  onSubmit(form: FormGroup): void {
    console.log("Form", form.value['username'])
    const { username, password } = form.value;
    this.authService.login(username, password).subscribe({
      next: data => {
        this.authService.saveAccessTokenData(data);
        this.tokenStorageService.saveUser(data);

        this.reloadPage();
      }
    });
  }
  reloadPage(): void {
    this.route.navigateByUrl('home');
  }
}
