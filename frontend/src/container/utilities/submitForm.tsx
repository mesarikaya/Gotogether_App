export async function submitForm(event: React.FormEvent<HTMLFormElement>): Promise<void> {

    // TODO: Add button disable
    const form = event.currentTarget;
    if (form.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
    }
}

export default submitForm;
