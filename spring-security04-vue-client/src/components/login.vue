<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const router = useRouter();

const username = ref('');
const password = ref('');

function doLogin() {
  const options = {
    url: '/api/login',
    data: {username: username.value, password: password.value},
    method: 'post',
    // headers: {'Content-Type': 'application/x-www-form-urlencoded'}
  }

  axios(options).then((res) => {
    console.log(res);

    if (res.data === "loginOK") {
      router.push('/');
    } else {
      alert('账号或密码错误');
    }
  })
}

</script>

<template>
  <h1 style="background-color: chocolate">欢迎登录</h1>
  用户名: <input type="text" v-model="username"><br/>
  密码: <input type="text" v-model="password"><br/>
  <button @click="doLogin">登录</button>
</template>

<style scoped></style>
