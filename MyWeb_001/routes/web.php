<?php

#se Illuminate\Foundation\Application;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\CategoryController;
use App\Http\Controllers\LessonController;
use App\Http\Controllers\RoleController;



Route::get('/', [DashboardController::class, 'index']);
// no auth routes
Route::middleware([ 'auth:sanctum', config('jetstream.auth_session'), 'verified', ])->group(function () {
    // auth routes
    Route::get('/dashboard', [DashboardController::class, 'dashboard'])->name('dashboard');
    //creacion, edicon, guardado, borrado etx super util
    Route::resource('/categories', CategoryController::class);
    Route::resource('/lessons', LessonController::class);
    Route::resource('/roles', RoleController::class);

});

