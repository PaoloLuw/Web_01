<script setup>
import ApplicationLogo from '@/Components/ApplicationLogo.vue';
</script>

<template>
<div>
    
<button v-if="$page.props.user.permissions.includes('create roles')">Create Roles</button> 
<button v-if="$page.props.user.permissions.includes('create lesson')">Create new Lesson</button>

</div>
</template>

<style>
button {
    
    background-color: pink;
    color: withe;
    padding: 20px;
    margin: 5px;
    
}
</style>
