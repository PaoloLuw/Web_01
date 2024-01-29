<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use Hash;
use Spatie\Permission\Models\Role;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $admin =User::create([
            'name'=>'admin',
            'email'=>'admin@speaksmarter.net',
            'password'=>FacadesHash::make('admin')
        ]);
        $admin->assignRole('admin');
        
        $editor =User::create([
            'name'=>'editor',
            'email'=>'editor@speaksmarter.net',
            'password'=>Hash::make('editor')
        ]);
        $editor->assignRole('editor');
    }
}
