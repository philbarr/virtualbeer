package com.simplyapped.virtualbeer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LoggingProxy<T> implements InvocationHandler
{
  final T underlying;

  public LoggingProxy(T underlying)
  {
    this.underlying = underlying;
  }

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> intf, final T obj)
  {
    System.out.println("T: " + obj.getClass().getClassLoader());
    return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] { intf }, new LoggingProxy<T>(obj));
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    StringBuffer sb = new StringBuffer();
    sb.append(method.getName());
    sb.append("(");
    for (int i = 0; args != null && i < args.length; i++)
    {
      if (i != 0) sb.append(", ");
      sb.append(args[i]);
    }
    sb.append(")");
    Object ret = method.invoke(underlying, args);
    if (ret != null)
    {
      sb.append(" -> ");
      sb.append(ret);
    }
    System.out.println(sb);
    return ret;
  }
}