package com.jl.config;

import com.jl.excption.CustomAccessDeniedHandler;
import com.jl.excption.CustomAuthExceptionEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 *
 *  我有一种设想就是 直接把过滤器加到
 * FilterSecurityInterceptor 后面 验证 角色-path 匹配信息就可以了。 而且 不管是 redis 还是jwt 访问认证中心
 * 将橘色信息放到redis 然后访问对应的模块 通过 token 他自己会验证 Authentication 从redis查询信息 注入 然后在调用
 * 直接就可以匹配了。我真觉得 做权限控制 一个 filter 就能完成了。
 */

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    CustomAuthExceptionEntryPoint customAuthExceptionEntryPoint;

//    @Autowired
//    DefaultFilterInvocationSecurityMetadataSource defaultFilterInvocationSecurityMetadataSource;




//    @Bean
//    public FilterInvocationSecurityMetadataSource securityMetadataSource(){
//
//        return new FilterInvocationSecurityMetadataSource1();
//    }




    /*
     *
     *   得到请求该路径 所需要的权限信息。 然后认证后 返回的 authentication 中 的 roles 做对比如果没有则抛出异常。 WebExpressionVoter 在
     *   voters 中第一个 来进行 放行 匹配。
     *
     *      他那个有什么问题呢 比如 一个路径 我明明 没有配置放行 但是 由于这个路径 我没有 配置 这个路径所需的 roles
     *      所以返回null  返回null 之后 那么根本就不会 走。 AccessDecisionManager 中的 decide 方法 对 path - role 进行匹配了。
     *      相当于 只要没配置 就是畅通无阻了。
     *
     * */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
//        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
//        // 处理需要放行的 path
//        voters.add(FangxAccessDecisionVoter());
//        // 处理 对应路径 需要 对应权限
//        voters.add(roleBasedVoter());
//        AffirmativeBased adm = new AffirmativeBased(voters);

        /**
         * DefaultFilterInvocationSecurityMetadataSource
         * 放行 和 需要认真的路径配置在这个里面 然后 由AbstractSecurityInterceptor 实现类 调用
         * 再带的AccessDecisionManager AffirmativeBased  其实就是调用DefaultFilterInvocationSecurityMetadataSource
         * 拿到对应路径 对应的配置 .anyRequest().authenticated(); 代表其他路径都要认证
         * 那么获取对应的 authentication 是否 isAuthentication 返回false 直接就异常了。
         */
      //  ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
                httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/user/register",
                        "/nacos/**",
                        "/user/getUserInfoByToken",
                        "/user/getMessageList"
                ).permitAll()
                .anyRequest().authenticated();
//                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
//                    @Override
//                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
//
//                        object.setAccessDecisionManager(adm);//注入用户登录权限
//                        object.setSecurityMetadataSource(securityMetadataSource());//注入用户所请求的地址所需要的权限
//                        return object;
//                    }
//                });
        /**
         *  。。精髓在这儿 如果我使用上面的 withObjectPostProcessor 那么直接相当于 把 原来的 给覆盖了。
         *  那么 我设置的 放行路径自然也就不生效了。 正因为 addBefore 仅仅只是 自定义 自己的 过滤逻辑
         *  其他的不变。只是靠springsecurity的过滤器 来完成 放行。
         *
         *  也就是说 我可以在自定义的这一层 过滤权限 spring自带的过滤 放行
         */
        //httpSecurity.addFilterAt(customFilterSecurityInterceptor, FilterSecurityInterceptor.class);

//
//        Field expressionHandler = ExpressionUrlAuthorizationConfigurer.class.getDeclaredField("expressionHandler");
//        expressionHandler.setAccessible(true);
//        SecurityExpressionHandler<FilterInvocation> securityExpressionHandler = (SecurityExpressionHandler<FilterInvocation>) expressionHandler.get(authenticated);
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(securityExpressionHandler);
//        voters.clear();
//        // 添加放行 路径匹配器
//        voters.add(webExpressionVoter);
//        // 添加自定义 url - role 匹配器
//        voters.add(roleBasedVoter());
    }


    @Bean(name = "tokenStore")
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resourceServerSecurityConfigurer) throws Exception {
        resourceServerSecurityConfigurer.tokenStore(tokenStore());

        resourceServerSecurityConfigurer.authenticationEntryPoint(customAuthExceptionEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);
    }






}
