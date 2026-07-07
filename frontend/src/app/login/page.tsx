import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { AuthTabs } from "@/components/auth/AuthTabs";

export default function LoginPage() {
  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <main className="flex justify-center px-10">
        <AuthTabs />
      </main>
    </div>
  );
}
