package com.mealkitcook.config;

//SecurityConfig.java의 configure 메소드에 설정을 추가하지 않으면 요청에 인증을 요구하지 않는다

import com.mealkitcook.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity  //WebSecurityConfigurerAdapter를 상속받는 클래스에 @EnableWebSecurity 어노테이션을 선언하면 SpringSecurityFilterChain이 자동 포함된다 상속받아서 메소드 오버라이딩을 통해 보안 설정을 커스터마이징 할 수 있다
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{   //http요청에 대한 보안을 설정 페이지권한설정, 로그인페이지설정, 로그아웃메소드등에 대한 설정을 작성
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            http.formLogin()
                    .loginPage("/members/login")    //로그인페이지 URL설정
                    .defaultSuccessUrl("/")     //로그인 성공시 이동할 URL을 설정
                    .usernameParameter("email")     //로그인시 사용할 파라미터 이름으로 eamil을 지정
                    .failureUrl("/members/login/error")     //로그인 실패 시 이동할 url
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))     //로그아웃 성공시 이동할 url
                    .logoutSuccessUrl("/")  //로그아웃성공하면 여기로 돌아간다
                    ;

            http.authorizeRequests()    //시큐리티처리에 HttpServletRequest를 이용한다는 것을 의미
                    .mvcMatchers("/", "/members/**","/item/**","/images/**","/brand/**","/board/**","/menu/**").permitAll()    //permitAll을 통해 모든 사용자가 인증없이 해당 경로에 접근할수 있도록 설정 메인페이지, 회원관련url, 상품상세페이지 상품이미지 불러오는 경로가 해당
                    .mvcMatchers("/admin/**").hasRole("ADMIN")  // /admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능
                    .anyRequest().authenticated()  //위 mvcMatchers에서 설정한 경로를 제외한 나머지 경로는 모두 인증을 요구하도록 설정
                    ;

            http.exceptionHandling()
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())    //인증되지 않은 사용자가 리소스에 접근했을때 수행되는 핸들러 등록
                    ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();
        //비밀번호를 데이터베이스에 그대로 저장했을 경우 데이터베이스가 해킹당하면 고객의 회원정보가 노출된다 이를 해결하기 위해 BCryptPasswordEncoder 해시함수를 이용하여 비밀번호를 암호화하여 저장한다
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{    //7
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());   //8
    }
    //7,8 Spring Security에서 인증은 AuthnticationManager를 통해 이루어 지며 AuthenticationManagerBuilder가 AuthenticationManager를 생성한다
    //userDetailService를 구현하고 있는 객체로 memberService를 지정해주며 비밀번호 암호화를 위해 passwordEncoder를 지정해준다

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**"); //static디렉터리의 하위 파일은 인증을 무시하도록 설정
    }
}
