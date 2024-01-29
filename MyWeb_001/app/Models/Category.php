<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    use HasFactory;
    protected $guarded= [];

    public function lessons(){
        return $this->belonsToMany(Lesson::class);//has por que no contiene la clave foranea
    }
}
